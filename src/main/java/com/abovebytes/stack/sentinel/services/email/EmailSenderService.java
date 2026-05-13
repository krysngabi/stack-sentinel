package com.abovebytes.stack.sentinel.services.email;

import com.abovebytes.stack.sentinel.entities.Property;
import com.abovebytes.stack.sentinel.models.Response;
import com.abovebytes.stack.sentinel.services.properties.PropertyService;
import com.abovebytes.stack.sentinel.utils.Constants;
import com.abovebytes.stack.sentinel.utils.MessageUtils;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.*;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import jakarta.mail.internet.MimeMessage;

@Service
@Log4j2
public class EmailSenderService {
    private Configuration freeMarkerCfg;
    private final JavaMailSender mailSender;
    private final PropertyService propertyService;
    private final StringTemplateLoader templateLoader;
    private final MessageUtils messageUtils;
    @Value("${above.bytes.cc.users}")
    private String ccUsers;

    public EmailSenderService(JavaMailSender mailSender, PropertyService propertyService, MessageUtils messageUtils) {
        this.mailSender = mailSender;
        this.propertyService = propertyService;
        this.messageUtils = messageUtils;
        this.templateLoader = new StringTemplateLoader();
        this.freeMarkerCfg = configFreeMarker(templateLoader);
    }

    String retrieveProperty(String templateName) {
        return propertyService.getProperty(templateName).map(Property::getValue).orElse(null);
    }

    public Response sendEmailDockerDown(String containerName) {
        String template = Constants.DOCKER_CONTAINER_DOWN_TEMPLATE;
        Map<String, Object> parameters = new HashMap<>();
        String templateId = propertyService.getProperty(template).map(Property::getValue).orElse(null);
        String to = propertyService.getProperty(Constants.CRITICAL_NOTIFICATION).map(Property::getValue).orElse(null);

        parameters.put("containerName", containerName);

        if (templateId == null) {
            Response responseAPI = new Response();
            responseAPI.setMessage("Template "  + template + " not found");

            log.error("Template not found");

            return responseAPI;
        }

        templateLoader.putTemplate(template, templateId);

        return sendEmail(to, "Docker container down", generateHtml(parameters, template), true);
    }

    private Configuration configFreeMarker(StringTemplateLoader templateLoader) {
        Version ourVersion = Configuration.VERSION_2_3_31;
        freemarker.template.Configuration configuration = new freemarker.template.Configuration(ourVersion);
        configuration.setObjectWrapper(new DefaultObjectWrapper(ourVersion));
        configuration.setDefaultEncoding("UTF-8");
        configuration.setOutputEncoding("UTF-8");
        configuration.setTemplateLoader(templateLoader);
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
        return configuration;
    }

    private String generateHtml(Map<String, Object> parameters, String templateId) {
        try {
            Template template = freeMarkerCfg.getTemplate(templateId);
            StringWriter writer = new StringWriter();
            template.process(parameters, writer);
            return writer.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private Response sendEmail(String to, String subject, String emailContentHtml, boolean shouldIncludeCopyCarbon) {
        Response responseAPI = new Response();
        try {
            final MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(emailContentHtml, "text/html; charset=utf-8");
            final Multipart mp = new MimeMultipart();
            final MimeMessage msg = mailSender.createMimeMessage();
            String from = propertyService.getProperty(Constants.SMTP_USERNAME).map(Property::getValue).orElse(null);
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            mp.addBodyPart(htmlPart);

            helper.setFrom(new InternetAddress(from));
            helper.setTo(to);

            if (shouldIncludeCopyCarbon) {
                String[] ccArray = ccUsers.split(",");
                for (String cCopy : ccArray) {
                    log.info("Copying {} to the email", cCopy);
                    if (!cCopy.isEmpty()) {
                        InternetAddress internetAddress = new InternetAddress(cCopy);
                        helper.addCc(internetAddress);
                    }
                }
            }

            helper.setSubject(subject);

            String plainTextContent = generatePlainTextFromHtml(emailContentHtml);

            helper.setText(plainTextContent, emailContentHtml);

            log.info("Sending email from {}", from);

            mailSender.send(msg);
            responseAPI.setStatus(true);
            responseAPI.setMessage(messageUtils.message("email.sent"));
            log.info("Email successfully sent to {}", to);
        } catch (MessagingException e) {
            log.error("Problem occurred while creating & sending email", e);
            responseAPI.setMessage("Problem occurred while creating & sending email " + e);
            throw new RuntimeException("Problem occurred while creating & sending email.", e);
        }
        return responseAPI;
    }

    private String generatePlainTextFromHtml(String html) {
        return html.replaceAll("<[^>]*>", "")  // remove all tags
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .trim();
    }

}
