package com.group1.MockProject.service;

public interface EmailService {
    void send(String to, String email);
    void sendNotification(String to, String email);
    void sendDetail(String to, String email, String subject);
    String buildEmail(String title, String name, String content);
    String buildApprovedCourseEmailForInstructor(String title, String instructorName);
    String buildApprovedCourseEmailForStudent(String title, String instructorName, String studentName);
    String buildRejectCourseEmail(String title, String name, String reason);
}
