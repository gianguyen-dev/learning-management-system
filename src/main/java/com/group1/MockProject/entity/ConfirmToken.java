package com.group1.MockProject.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "confirm_token")
public class ConfirmToken {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "token")
  private String token;

  @Column(name = "confirmed_at")
  private LocalDateTime confirmedAt;

  @Column(name = "expires_at")
  private LocalDateTime expiresAt;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(nullable = false, name = "user_id")
  private User user;

  public ConfirmToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, User user) {
    this.token = token;
    this.user = user;
    this.createdAt = createdAt;
    this.expiresAt = expiresAt;
  }
}
