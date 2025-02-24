package com.group1.MockProject.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "Instructors")
public class Instructor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Column(nullable = false, length = 200)
  private String name;

  @Column(nullable = false, length = 200)
  private String expertise;

  @JsonBackReference
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;

  @JsonBackReference
  @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Course> courses;
}
