package com.pwdk.grocereach.product.domains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
@Filter(name = "deletedAtNull", condition = "deleted_at is Null")
public class Product {
  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @NotNull
  @Column(name = "name")
  private String name;

  @NotNull
  @Column(name = "description")
  private String description;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "current_version_id", referencedColumnName = "id")
  private ProductVersions currentVersion;

  @NotNull
  @Column(name = "is_active")
  private boolean isActive;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @Column(name = "deleted")
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
