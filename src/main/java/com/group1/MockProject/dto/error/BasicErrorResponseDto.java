package com.group1.MockProject.dto.error;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicErrorResponseDto {
  private int status;
  private String message;
  private ResponseDetails response;
  private LocalDateTime timestamp;

  public BasicErrorResponseDto(int status, String message, String responseMessage) {
    this.status = status;
    this.message = message;
    this.timestamp = LocalDateTime.now();
    this.response = new ResponseDetails(responseMessage);
  }

  public static class ResponseDetails {
    private final String responseMessage;

    public ResponseDetails(String message) {
      this.responseMessage = message;
    }

    // Getter
    public String getMessage() {
      return responseMessage;
    }
  }
}
