package com.group1.MockProject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "status")
  private int status;

  @JsonBackReference
  @ManyToOne
  @JoinColumn(name = "student_id", referencedColumnName = "id")
  private Student student;

  @Column(name = "payment_code")
  private String paymentCode;

  @Column(name = "payment_date")
  private LocalDateTime payment_date;

  @JsonManagedReference
  @OneToMany(mappedBy = "payment")
  private Set<PaymentDetail> paymentDetails;

  @Column(name = "total_price")
  private Long total_price;
}
