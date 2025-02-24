package com.group1.MockProject.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "analytics")
public class Analytic {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(name = "visitor", nullable = true)
  private int visitorCount = 0;

  @Column(name = "sub", nullable = false)
  private int subscriptionCount;

  @Column(name = "sales", nullable = false)
  private Double sales;

  @ManyToOne
  @JoinColumn(name = "instructor_id", nullable = false)
  private Instructor instructor;
}
