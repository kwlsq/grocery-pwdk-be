package com.pwdk.grocereach.Auth.Infrastructure.specifications;

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Domain.Enums.UserRole;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

  public static Specification<User> searchByKeyword(String keyword, UserRole role) {
    return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      Predicate predicate = cb.conjunction();

      if (keyword != null && !keyword.isEmpty()) {
        predicate = cb.and(predicate,
            cb.like(cb.lower(root.get("fullName")), "%" + keyword.toLowerCase() + "%"));
      }

      if (role != null) {
        predicate = cb.and(predicate,
            cb.equal(root.get("role"), role));
      }

      return predicate;
    };
  }

  public static Specification<User> isNotDeleted() {
    return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      return cb.isNull(root.get("deletedAt"));
    };
  }

  public static Specification<User> getFilteredUsers(String searchText, UserRole role) {
    return isNotDeleted().and(searchByKeyword(searchText, role));
  }
}