package com.pwdk.grocereach.store.domains.entities;

import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "warehouses")
@Filter(name = "deletedAtNull", condition = "deleted_at is Null")
public class Warehouse {
  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @NotNull
  @Column(name = "name")
  private String name;

  @NotNull
  @Column(name = "address")
  private String address;

  @NotNull
  @Column(name = "latitude")
  private double latitude;

  @NotNull
  @Column(name = "longitude")
  private double longitude;

  @Column(name = "is_active")
  private boolean isActive;

  @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
  private List<Inventory> inventories;

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
