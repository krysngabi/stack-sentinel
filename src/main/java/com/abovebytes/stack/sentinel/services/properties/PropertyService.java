package com.abovebytes.stack.sentinel.services.properties;

import com.abovebytes.stack.sentinel.entities.Property;
import com.abovebytes.stack.sentinel.enums.AllowedApps;
import com.abovebytes.stack.sentinel.repositories.PropertyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PropertyService {
    private final PropertyRepository propertyRepository;

    public Optional<Property> getProperty(String name) {
        return propertyRepository.findByName(name);
    }


    public Optional<Property> getProperty(String name, AllowedApps app) {
        return propertyRepository.findByNameAndAppName(name, app);
    }

    public Optional<Property> getNonAdminProperty(String name) {
        return propertyRepository.findByNameAndIsForAdminOnlyFalse(name);

        // 2. Filter based on Admin requirements
//        return propertyOpt.filter(property -> {
//            // If it's NOT admin-only, anyone can see it
//            if (!property.isForAdminOnly()) {
//                return true;
//            }
//
//            // If it IS admin-only, check if the current user has the ADMIN role
//            // Assuming SecurityUtils is where your hasRole method lives
//            return AuthorizationUtils.hasRole(RoleValue.SUPER_ADMIN);
//        });
    }


    public Optional<Property> getNonAdminProperty(String name, AllowedApps app) {
        return propertyRepository.findByNameAndAppNameAndIsForAdminOnlyFalse(name, app);

        // 2. Filter based on Admin requirements
//        return propertyOpt.filter(property -> {
//            // If it's NOT admin-only, anyone can see it
//            if (!property.isForAdminOnly()) {
//                return true;
//            }
//
//            // If it IS admin-only, check if the current user has the ADMIN role
//            // Assuming SecurityUtils is where your hasRole method lives
//            return AuthorizationUtils.hasRole(RoleValue.SUPER_ADMIN);
//        });
    }
}
