package com.abovebytes.stack.sentinel.services;

import com.abovebytes.stack.sentinel.entities.Property;
import com.abovebytes.stack.sentinel.models.ContainerFailure;
import com.abovebytes.stack.sentinel.models.Response;
import com.abovebytes.stack.sentinel.services.email.EmailSenderService;
import com.abovebytes.stack.sentinel.services.properties.PropertyService;
import com.abovebytes.stack.sentinel.utils.Constants;
import com.abovebytes.stack.sentinel.utils.ProfileChecker;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Service
public class MonitorService {

    private final EmailSenderService emailSenderService;
    private final PropertyService propertyService;
    private final DockerClient dockerClient;
    private final TaskScheduler scheduler;
    private final ProfileChecker profileChecker;

    private ScheduledFuture<?> scheduledTask;

    public MonitorService(EmailSenderService emailSenderService, PropertyService propertyService, TaskScheduler scheduler, ProfileChecker profileChecker) {
        this.emailSenderService = emailSenderService;
        this.propertyService = propertyService;
        this.scheduler = scheduler;
        this.profileChecker = profileChecker;

        var config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        var httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45))
                .build();

        this.dockerClient = DockerClientImpl.getInstance(config, httpClient);
    }

    @PostConstruct
    public void startScheduler() {
        scheduleTaskWithInterval();
    }

    private void scheduleTaskWithInterval() {
        // Cancel existing task if running
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(false);
        }

        // Get interval from DB (default to n mins if missing or invalid)
        long minutes = getIntervalMinutesFromDb().orElse(15L);

        log.info("Scheduling docker monitor task to run every {} minute(s)", minutes);

        scheduledTask = scheduler.scheduleAtFixedRate(this::checkContainers, Duration.ofMinutes(minutes));
    }

    private Optional<Long> getIntervalMinutesFromDb() {
        try {
            return propertyService.getProperty(Constants.MONITOR_INTERVAL_MINUTES)
                    .map(property -> Long.parseLong(property.getValue()));
        } catch (NumberFormatException e) {
            log.warn("Invalid monitor interval hours value in DB", e);
            return Optional.empty();
        }
    }

    public void checkContainers() {
        log.info("Checking containers in docker started at {}", LocalDateTime.now());
        try {
            Property containerList = propertyService.getProperty(Constants.DOCKER_CONTAINER_LIST).orElse(null);

            if (Arrays.asList(profileChecker.getActiveProfiles()).contains("local")) {
                log.info("Skipping local docker container check");
//           return;
            }

            if (containerList != null) {
                List<String> containersToMonitor = List.of(containerList.getValue().split(","));
                log.info("Found {} containers to monitor in docker {}", containersToMonitor.size(), containersToMonitor);
                List<Container> runningContainers = dockerClient.listContainersCmd()
                        .withShowAll(true)
                        .exec();

                List<String> containerNames = runningContainers.stream()
                        .flatMap(c -> Arrays.stream(c.getNames()))
                        .map(name -> name.startsWith("/") ? name.substring(1) : name)
                        .toList();

                log.info("Found {} running docker containers: {}", containerNames.size(), containerNames);

                List<ContainerFailure> failedContainers = new ArrayList<>();

                for (String containerName : containersToMonitor) {
                    Optional<Container> containerOpt = runningContainers.stream()
                            .filter(c -> Arrays.asList(c.getNames()).contains("/" + containerName))
                            .findFirst();

                    if (containerOpt.isEmpty()) {
                        log.error("Container {} is MISSING from the host!", containerName);
                        failedContainers.add(new ContainerFailure(containerName, "Missing", "Container not found on host.", "Container not found on host."));
                        continue;
                    }

                    Container c = containerOpt.get();
                    String state = c.getState(); // e.g., "exited", "running", "paused"
                    String status = c.getStatus(); // e.g., "Up 2 hours (unhealthy)"

                    boolean isUnhealthy = status.contains("(unhealthy)");
                    boolean isNotRunning = !state.equalsIgnoreCase("running");

                    if (isNotRunning || isUnhealthy) {
                        String reason = isUnhealthy ? "Healthcheck failed" : status;
                        log.warn("Alerting: {} is {}", containerName, reason);

                        failedContainers.add(new ContainerFailure(containerName, state, status, reason));
                    }
                }

                if (!failedContainers.isEmpty()) {
                    // Send ONE email containing the whole list
                    Response response = emailSenderService.sendBulkAlert(failedContainers);
                    log.info("Bulk email sent: {}", response.getMessage());
                } else {
                    log.info("No containers are unhealthy or missing.");
                }
            }
        } catch (Exception e) {
            log.error("Failed to connect to Docker daemon: {}", e.getMessage());
            // Optionally alert that the MONITOR itself is having issues
        }
    }

    // Optional: call this method to reload interval without restarting app
    public void reloadInterval() {
        scheduleTaskWithInterval();
    }
}
