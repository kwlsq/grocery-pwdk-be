package com.pwdk.grocereach.common.exception;

import com.pwdk.grocereach.common.Response;
import org.springframework.http.HttpStatus;
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

  @ExceptionHandler(MissingParameterException.class)
  public ResponseEntity<?> handleMissingParameter(MissingParameterException e) {
    return ResponseEntity.badRequest().body(
        Response.failedResponse(e.getMessage())
    );
  }

  @ExceptionHandler(CategoryNotFoundException.class)
  public ResponseEntity<?> handleCategoryNotFound(CategoryNotFoundException e) {
    return ResponseEntity.status(404).body(
        Response.failedResponse("Category not found!")
    );
  }

  @ExceptionHandler(ProductAlreadyExistException.class)
  public ResponseEntity<?> handleProductAlreadyExist(ProductAlreadyExistException e) {
    return ResponseEntity.status(404).body(
        Response.failedResponse("Product with the same name already exist!")
    );
  }

  @ExceptionHandler(WarehouseNotFoundException.class)
  public ResponseEntity<?> handleWarehouseNotFound(WarehouseNotFoundException e) {
    return ResponseEntity.status(404).body(
        Response.failedResponse(e.getMessage())
    );
  }

  @ExceptionHandler(StoreNotFoundException.class)
  public ResponseEntity<?> handleStoreNotFound(StoreNotFoundException e) {
    return ResponseEntity.status(404).body(
        Response.failedResponse(e.getMessage())
    );
  }

  @ExceptionHandler(CategoryAlreadyExistException.class)
  public ResponseEntity<?> handleCategoryAlreadyExists(CategoryAlreadyExistException e) {
    return ResponseEntity.status(404).body(
        Response.failedResponse("Category with the same name already exist!")
    );
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<?> handleUserNotFound(UserNotFoundException e) {
    return ResponseEntity.status(404).body(
        Response.failedResponse(e.getMessage())
    );
  }
}
