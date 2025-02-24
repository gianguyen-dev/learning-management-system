package com.group1.MockProject.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses")
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int Id;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "description", columnDefinition = "VARCHAR(255) DEFAULT 'Default description'")
  private String description = "Default description";

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @NotNull
  @Column(name = "status")
  private int status;

  @Column(name = "rejection_reason", nullable = true)
  private String rejectionReason;

  @NotNull
  @Column(name = "price")
  private Double price;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "instructor_id", referencedColumnName = "id", nullable = false)
  private Instructor instructor;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
  private Category category;

  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Enrollment> enrollment;

  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<PaymentDetail> paymentDetails;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Report> report;
    @JsonManagedReference
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedCourse> savedCourses;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();
    @JsonManagedReference
  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MyCourse> myCourses;

  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
  private List<Review> reviews = new ArrayList<>();

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
