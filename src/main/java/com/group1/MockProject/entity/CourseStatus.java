package com.group1.MockProject.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CourseStatus {
  PENDING(0),
  APPROVED(1),
  REJECTED(2);

  private final int value;
}
