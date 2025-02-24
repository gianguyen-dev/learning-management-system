package com.group1.MockProject.service.implementation;

import static org.mockito.Mockito.*;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

  @Mock private JavaMailSender mailSender;

  @InjectMocks private EmailServiceImpl emailService;

  @BeforeEach
  void setUp() {}

  @Test
  public void testSend_Success() throws Exception {
    // Arrange
    String to = "test@example.com";
    String emailContent = "<p>This is a test email</p>";

    MimeMessage mimeMessage = mock(MimeMessage.class);
    MimeMessageHelper mimeMessageHelper = mock(MimeMessageHelper.class);

    when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    // Act
    emailService.send(to, emailContent);

    // Assert
    ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
    verify(mailSender, times(1)).send(messageCaptor.capture());
    verify(mailSender, times(1)).createMimeMessage();
  }

  @Test
  public void testSendDetail_Success() throws Exception {
    // Arrange
    String to = "test@example.com";
    String emailContent = "<p>This is a test email</p>";
    String subject = "Test Subject";

    MimeMessage mimeMessage = mock(MimeMessage.class);
    MimeMessageHelper mimeMessageHelper = mock(MimeMessageHelper.class);

    lenient().when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

    // Act
    emailService.sendDetail(to, emailContent, subject);

    // Assert
    verify(mailSender, times(1)).createMimeMessage();
    verify(mailSender, times(1)).send(mimeMessage);
  }
}
