package com.pwdk.grocereach.shipping.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipping_costs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingCost {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private Integer originCityId;

    @Column(nullable = false)
    private Integer destinationCityId;

    @Column(nullable = false)
    private String courier;

    @Column(nullable = false)
    private String service;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private Long cost;
    @Column(nullable = false)
    private String etd;
    @Column(nullable = false)
    private Integer weight;
    private String description;
    @Builder.Default
    private Boolean isActive = true;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime lastUpdatedFromApi;
}