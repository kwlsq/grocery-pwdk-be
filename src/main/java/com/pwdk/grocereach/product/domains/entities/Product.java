package com.pwdk.grocereach.product.domains.entities;

import com.pwdk.grocereach.image.domains.entities.ProductImages;
import com.pwdk.grocereach.store.domains.entities.Stores;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.Where;

import java.time.Instant;
import java.util.*;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product")
@FilterDef(name = "deletedAtFilter")
@Filter(name = "deletedAtFilter", condition = "deleted_at IS NULL")
@Where(clause = "deleted_at IS NULL")
public class Product {
  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @NotNull
  @Column(name = "name")
  private String name;

  @ManyToOne
  @JoinColumn(name = "store_id")
  private Stores store;

  @NotNull
  @Column(name = "description")
  private String description;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "current_version_id", referencedColumnName = "id")
  private ProductVersions currentVersion;

  @OneToMany(mappedBy = "product")
  private List<ProductImages> productImages = new ArrayList<>();

  @OneToMany(mappedBy = "product")
  private Set<ProductPromotions> productPromotions = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "category_id")
  private ProductCategory category;

  @NotNull
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
