package com.pwdk.grocereach.store.infrastructures.specifications;

import com.pwdk.grocereach.store.domains.entities.Stores;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

public class StoreSpecification {
  public static Specification<Stores> searchByKeyword(String keyword) {
    return (Root<Stores> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

      Predicate keywordPredicate = cb.conjunction();

      if (keyword != null && !keyword.isEmpty()) {
        keywordPredicate = cb.or(
            cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
            cb.like(cb.lower(root.get("address")), "%" + keyword.toLowerCase() + "%")
        );
      }

      return keywordPredicate;
    };
  }

  public static Specification<Stores> isNotDeleted() {
    return (Root<Stores> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      return cb.isNull(root.get("deletedAt"));
    };
  }

  public static Specification<Stores> getFilteredStore(String searchText) {
    return isNotDeleted().and(searchByKeyword(searchText));
  }
}
