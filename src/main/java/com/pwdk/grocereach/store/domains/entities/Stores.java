package com.pwdk.grocereach.store.domains.entities;

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.product.domains.entities.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stores")
@Filter(name = "deletedAtNull", condition = "deleted_at is Null")
public class Stores {
  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @OneToMany(mappedBy = "store")
  private List<Warehouse> warehouses = new ArrayList<>();

  @OneToMany(mappedBy = "store")
  private List<Product> products = new ArrayList<>();

  @Column(name = "store_name")
  private String storeName;

  @OneToOne
  @JoinColumn(name = "manager_id", unique = true)
  private User admin;

  @Column(name = "description")
  private String description;

  @Column(name = "address")
  private String address;

  @Column(name = "latitude")
  private double latitude;

  @Column(name = "longitude")
  private double longitude;

  @Column(name = "is_active")
  private boolean isActive;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = Instant.now();
  }

  @PreRemove
  public void preRemove() {
    this.deletedAt = Instant.now();
  }
}
