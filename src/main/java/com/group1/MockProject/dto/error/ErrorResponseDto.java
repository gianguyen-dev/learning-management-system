package com.group1.MockProject.dto.error;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseDto extends BasicErrorResponseDto {
  private Map<String, String> errors;

  public ErrorResponseDto(int status, String message, String responseMessage) {
    super(status, message, responseMessage);
  }
}
