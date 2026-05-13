package com.abovebytes.stack.sentinel.services;

import com.abovebytes.stack.sentinel.entities.Property;
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

        // Get interval from DB (default to 1 hour if missing or invalid)
        long intervalHours = getIntervalHoursFromDb().orElse(1L);

        log.info("Scheduling docker monitor task to run every {} hours", intervalHours);

        scheduledTask = scheduler.scheduleAtFixedRate(this::checkContainers, Duration.ofHours(intervalHours));
    }

    private Optional<Long> getIntervalHoursFromDb() {
        try {
            return propertyService.getProperty(Constants.MONITOR_INTERVAL_HOURS)
                    .map(property -> Long.parseLong(property.getValue()));
        } catch (NumberFormatException e) {
            log.warn("Invalid monitor interval hours value in DB", e);
            return Optional.empty();
        }
    }

    public void checkContainers() {
        log.info("Checking containers in docker started at {}", LocalDateTime.now());
        Property containerList = propertyService.getProperty(Constants.DOCKER_CONTAINER_LIST).orElse(null);

        if (Arrays.asList(profileChecker.getActiveProfiles()).contains("local")) {
            log.info("Skipping local docker container check");
           return;
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

            log.info("Found {} containers: {}", containerNames.size(), containerNames);

            for (String containerName : containersToMonitor) {
                boolean isRunning = runningContainers.stream()
                        .anyMatch(c -> Arrays.asList(c.getNames()).contains("/" + containerName)
                                && c.getState().equalsIgnoreCase("running"));

                if (!isRunning) {
                    log.info("Container stopped ror not running {}", containerName);
                    emailSenderService.sendEmailDockerDown(containerName);
                }
            }
        }
    }

    // Optional: call this method to reload interval without restarting app
    public void reloadInterval() {
        scheduleTaskWithInterval();
    }
}
