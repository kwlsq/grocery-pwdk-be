package com.pwdk.grocereach.common.exception;

public class StoreNotFoundException extends RuntimeException {
  public StoreNotFoundException(String message) {
    super(message);
  }
}
