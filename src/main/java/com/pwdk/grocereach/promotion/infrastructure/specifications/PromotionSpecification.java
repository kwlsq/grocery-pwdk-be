package com.pwdk.grocereach.promotion.infrastructure.specifications;

import com.pwdk.grocereach.promotion.domain.entities.Promotions;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class PromotionSpecification {
  public static Specification<Promotions> searchByKeyword(String keyword) {
    return (Root<Promotions> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

      Predicate keywordPredicate = cb.conjunction();

      if (keyword != null && !keyword.isEmpty()) {
        keywordPredicate = cb.or(
            cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
            cb.like(cb.lower(root.get("description")), "%" + keyword.toLowerCase() + "%")
        );
      }

      return keywordPredicate;
    };
  }

  public static Specification<Promotions> isNotDeleted() {
    return (Root<Promotions> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      return cb.isNull(root.get("deletedAt"));
    };
  }

  public static Specification<Promotions> getFilteredStore(String searchText) {
    return isNotDeleted().and(searchByKeyword(searchText));
  }
}
