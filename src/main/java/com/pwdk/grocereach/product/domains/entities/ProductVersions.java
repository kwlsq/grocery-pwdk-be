package com.pwdk.grocereach.product.domains.entities;

import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_version")
@Filter(name = "deletedAtFilter", condition = "deleted_at is null")
public class ProductVersions {
  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @NotNull
  @Column(name = "price")
  private BigDecimal price;

  @NotNull
  @Column(name = "weight")
  private BigDecimal weight;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @OneToMany(mappedBy = "productVersion", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<Inventory> inventories;

  @NotNull
  @Column(name = "version_number")
  private Integer versionNumber;

  @NotNull
  @Column(name = "change_reason")
  private String changeReason;

  @NotNull
  @Column(name = "effective_from")
  private Instant effectiveFrom;

  @Column(name = "effective_to")
  private Instant effectiveTo;

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
