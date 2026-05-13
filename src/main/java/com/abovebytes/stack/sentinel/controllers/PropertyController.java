package com.abovebytes.stack.sentinel.controllers;

import com.abovebytes.stack.sentinel.entities.Property;
import com.abovebytes.stack.sentinel.enums.AllowedApps;
import com.abovebytes.stack.sentinel.exception.StackSentinelException;
import com.abovebytes.stack.sentinel.services.properties.PropertyService;
import com.abovebytes.stack.sentinel.utils.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping(path = "/properties")
@CrossOrigin
@AllArgsConstructor
public class PropertyController {
    private final MessageUtils messageUtils;
    private final PropertyService propertyService;


    @GetMapping(path = "/search-name")
    public HashMap<String, String> getProperties(@RequestParam(name = "name") String name) {
        try {
            HashMap<String,String> propertyDto = new HashMap<>();

            Optional<Property> property = propertyService.getNonAdminProperty(name);

            propertyDto.put(
                    property.isPresent() ? name : messageUtils.message("error"), property.map(Property::getValue).orElse(name + " " + messageUtils.message("not.found")));

            return propertyDto;
        } catch (Exception e) {
            log.error("An error occurred while fetching property with name {}", name, e);
            return null;
        }
    }

    @GetMapping(path = "/search-app")
    public HashMap<String, String> getProperties(@RequestParam(name = "name") String name, @RequestParam(name = "application") String application) {
        try {
            HashMap<String,String> propertyDto = new HashMap<>();

            AllowedApps allowedApp = null;

            if (application != null && !application.trim().isEmpty()) {
                allowedApp =
                        AllowedApps.fromString(
                                application,
                                messageUtils,
                                LocaleContextHolder.getLocale()
                        );
            }

            if (allowedApp == null) {
                throw new StackSentinelException(HttpStatus.BAD_REQUEST, messageUtils.message("invalid.app.name", application));
            }

            Optional<Property> property = propertyService.getNonAdminProperty(name, allowedApp);

            propertyDto.put(
                    property.isPresent() ? name : messageUtils.message("error"), property.map(Property::getValue).orElse(name + " " + messageUtils.message("not.found")));

            return propertyDto;
        } catch (Exception e) {
            if (e instanceof StackSentinelException) {
                throw e;
            }

            log.error("An error occurred while fetching property with name {}: and application {}", name, application, e);
            return null;
        }
    }
}
