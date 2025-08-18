package com.pwdk.grocereach.product.infrastructures.specification;

import com.pwdk.grocereach.product.domains.entities.Product;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class ProductSpecification {
  public static Specification<Product> searchByKeyword(String keyword,UUID category, UUID storeID) {
    return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

      Predicate keywordPredicate = cb.conjunction();

      if (keyword != null && !keyword.isEmpty()) {
        keywordPredicate = cb.or(
            cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
            cb.like(cb.lower(root.get("description")), "%" + keyword.toLowerCase() + "%")
        );
      }

      if (category != null) {
        Predicate categoryPredicate = cb.equal(root.get("category").get("id"), category);
        return cb.and(keywordPredicate, categoryPredicate);
      }

      if (storeID != null) {
        Predicate categoryPredicate = cb.equal(root.get("store").get("id"), storeID);
        return cb.and(keywordPredicate, categoryPredicate);
      }

      return keywordPredicate;
    };
  }

  public static Specification<Product> isNotDeleted() {
    return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      return cb.isNull(root.get("deletedAt"));
    };
  }

  public static Specification<Product> getFilteredProduct(String searchText, UUID category, UUID storeID) {
    return isNotDeleted().and(searchByKeyword(searchText, category, storeID));
  }
}
