package com.abovebytes.stack.sentinel.entities;


import com.abovebytes.stack.sentinel.enums.AllowedApps;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(
        name = "properties",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name"}, name = "UQ_property_name"), // Your existing one
                @UniqueConstraint(columnNames = {"name", "app_name"}, name = "UQ_name_app_name") // The new combo
        }
)
@NoArgsConstructor
public class Property implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "property_id")
    private int propertyId;

    @Basic(optional = false)
    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "app_name")
    private AllowedApps appName;

    @Basic(optional = false)
    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value;

    @Column(name = "is_for_admin_only", nullable = false)
    private boolean isForAdminOnly = false;
}
