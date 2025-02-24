package com.group1.MockProject.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "cartitems")
public class CartItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  private Double price;

  @Column(name = "discount_price")
  private Double discountPrice;

  @ManyToOne
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  @ManyToOne
  @JoinColumn(name = "course_id", nullable = false)
  private Course course;
}
