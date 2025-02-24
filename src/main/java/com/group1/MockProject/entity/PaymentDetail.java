package com.group1.MockProject.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minidev.json.annotate.JsonIgnore;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "paymentdetails")
public class PaymentDetail {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "comment", columnDefinition = "VARCHAR(255) DEFAULT 'Default comment'")
  private String comment = "Default comment";

  @Column(name = "payment_date")
  private LocalDate payment_date;

  @ManyToOne
  @JoinColumn(name = "payment_id", referencedColumnName = "id")
  private Payment payment;

  @ManyToOne
  @JoinColumn(name = "student_id", referencedColumnName = "id")
  private Student student;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;
}
