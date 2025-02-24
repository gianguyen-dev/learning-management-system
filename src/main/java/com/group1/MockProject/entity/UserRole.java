package com.group1.MockProject.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum UserRole {
  STUDENT,
  INSTRUCTOR,
  ADMIN;

  public GrantedAuthority toGrantedAuthority() {
    return new SimpleGrantedAuthority("ROLE_" + this.name());
  }
}
