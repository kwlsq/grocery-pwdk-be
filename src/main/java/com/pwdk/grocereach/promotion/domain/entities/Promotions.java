package com.pwdk.grocereach.promotion.domain.entities;

import com.pwdk.grocereach.promotion.domain.enums.PromotionType;
import com.pwdk.grocereach.promotion.domain.enums.PromotionUnit;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "promotions")
@Filter(name = "deletedAtNull", condition = "deleted_at is Null")
public class Promotions {
  @Id
  @GeneratedValue
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private PromotionType type;

  @Column(name = "value")
  private BigDecimal value;

  @Column(name = "unit")
  @Enumerated(EnumType.STRING)
  private PromotionUnit unit;

  @Column(name = "min_purchase")
  private BigDecimal minPurchase;

  @Column(name = "start_at")
  private Instant startAt;

  @Column(name = "end_at")
  private Instant endAt;

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
