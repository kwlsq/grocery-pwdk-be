package com.pwdk.grocereach.common.exception;

import com.pwdk.grocereach.common.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<?> handleProductNotFound(ProductNotFoundException e) {
    return ResponseEntity.status(404).body(
        Response.failedResponse("Product not found!")
    );
  }
}
