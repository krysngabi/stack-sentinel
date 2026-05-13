package com.abovebytes.stack.sentinel.repositories;

import com.abovebytes.stack.sentinel.entities.Property;
import com.abovebytes.stack.sentinel.enums.AllowedApps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Integer> {
    Optional<Property> findByName(String propertyName);
    Optional<Property> findByNameAndAppName(String propertyName, AllowedApps allowedApp);

    Optional<Property> findByNameAndIsForAdminOnlyFalse(String propertyName);

    // Fetch by name, app, and ensure it's not admin-only
    Optional<Property> findByNameAndAppNameAndIsForAdminOnlyFalse(String propertyName, AllowedApps allowedApp);
}
