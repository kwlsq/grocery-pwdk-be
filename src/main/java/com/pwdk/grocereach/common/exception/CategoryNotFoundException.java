package com.pwdk.grocereach.common.exception;

public class CategoryNotFoundException extends RuntimeException {
  public CategoryNotFoundException(String message) {
    super(message);
  }
}
