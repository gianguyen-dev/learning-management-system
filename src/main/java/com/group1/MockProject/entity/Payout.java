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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payouts")
public class Payout {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "amount")
  private Double amount;

  @Column(name = "payout_date")
  private LocalDateTime payout_date;

  @OneToOne
  @JoinColumn(name = "instructor_id", referencedColumnName = "id")
  private Instructor instructor;
}
