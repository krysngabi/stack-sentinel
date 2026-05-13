package com.abovebytes.stack.sentinel;

import com.abovebytes.stack.sentinel.entities.Property;
import com.abovebytes.stack.sentinel.services.properties.PropertyService;
import com.abovebytes.stack.sentinel.utils.Constants;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import java.util.Properties;

@SpringBootApplication(scanBasePackages = "com.abovebytes.stack.sentinel")
@EnableScheduling
public class StackSentinel {
    @Autowired
    PropertyService propertyService;

    public static void main(String[] args) {
        SpringApplication.run(StackSentinel.class, args);
    }

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setMaxPayloadLength(64000);
        return loggingFilter;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();

        sender.setHost(propertyService.getProperty(Constants.SMTP_HOST)
                .map(Property::getValue)
                .orElse("mail.abovebytes.com"));

        sender.setPort(465);  // SSL port

        sender.setUsername(propertyService.getProperty(Constants.SMTP_USERNAME)
                .map(Property::getValue)
                .orElse("notifications@abovebytes.com"));

        sender.setPassword(propertyService.getProperty(Constants.SMTP_PASSWORD)
                .map(Property::getValue)
                .orElse("your-email-password"));

        Properties props = getProperties(sender);

        sender.setJavaMailProperties(props);

        return sender;
    }

    private static @NonNull Properties getProperties(JavaMailSenderImpl sender) {
        Properties props = sender.getJavaMailProperties();

        props.put("mail.transport.protocol", "smtps");  // Use SMTPS for port 465
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");       // Enable SSL explicitly
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.starttls.enable", "false"); // Disable STARTTLS for port 465

        props.put("mail.debug", "false");
        return props;
    }
}
