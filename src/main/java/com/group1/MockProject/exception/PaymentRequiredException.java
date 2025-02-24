package com.group1.MockProject.exception;

public class PaymentRequiredException extends RuntimeException {
  public PaymentRequiredException(String message) {
    super(message);
  }
}
