package com.group1.MockProject.service.implementation;

import com.group1.MockProject.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

  JavaMailSender mailSender;

  @Override
  @Async
  public void send(String to, String email) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      helper.setText(email, true);
      helper.setTo(to);
      helper.setSubject("Xác nhận địa chỉ email của bạn");
      helper.setFrom("admin@group1.com");
      mailSender.send(mimeMessage);

    } catch (Exception e) {
      throw new RuntimeException("Gửi email thất bại: " + e);
    }
  }

  @Override
  @Async
  public void sendNotification(String to, String email) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      helper.setText(email, true);
      helper.setTo(to);
      helper.setSubject("Thông báo từ Cursus");
      helper.setFrom("admin@group1.com");
      mailSender.send(mimeMessage);

    } catch (Exception e) {
      throw new RuntimeException("Gửi email thất bại: " + e);
    }
  }

  @Override
  public void sendDetail(String to, String email, String subject) {
    try {
      MimeMessage mimeMessage = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      helper.setText(email, true);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setFrom("admin@group1.com");
      mailSender.send(mimeMessage);

    } catch (Exception e) {
      throw new RuntimeException("Gửi email thất bại: " + e);
    }
  }

  @Override
  public String buildEmail(String title, String name, String content) {
    return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n"
        + "\n"
        + "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n"
        + "\n"
        + "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n"
        + "    <tbody><tr>\n"
        + "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n"
        + "        \n"
        + "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n"
        + "          <tbody><tr>\n"
        + "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n"
        + "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
        + "                  <tbody><tr>\n"
        + "                    <td style=\"padding-left:10px\">\n"
        + "                  \n"
        + "                    </td>\n"
        + "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n"
        + "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">"
        + title
        + "</span>\n"
        + "                    </td>\n"
        + "                  </tr>\n"
        + "                </tbody></table>\n"
        + "              </a>\n"
        + "            </td>\n"
        + "          </tr>\n"
        + "        </tbody></table>\n"
        + "        \n"
        + "      </td>\n"
        + "    </tr>\n"
        + "  </tbody></table>\n"
        + "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
        + "    <tbody><tr>\n"
        + "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n"
        + "      <td>\n"
        + "        \n"
        + "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n"
        + "                  <tbody><tr>\n"
        + "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n"
        + "                  </tr>\n"
        + "                </tbody></table>\n"
        + "        \n"
        + "      </td>\n"
        + "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n"
        + "    </tr>\n"
        + "  </tbody></table>\n"
        + "\n"
        + "\n"
        + "\n"
        + "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n"
        + "    <tbody><tr>\n"
        + "      <td height=\"30\"><br></td>\n"
        + "    </tr>\n"
        + "    <tr>\n"
        + "      <td width=\"10\" valign=\"middle\"><br></td>\n"
        + "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n"
        + "        \n"
        + "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Dear "
        + name
        + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> "
        + content
        + "        \n"
        + "      </td>\n"
        + "      <td width=\"10\" valign=\"middle\"><br></td>\n"
        + "    </tr>\n"
        + "    <tr>\n"
        + "      <td height=\"30\"><br></td>\n"
        + "    </tr>\n"
        + "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n"
        + "\n"
        + "</div></div>";
  }

  public String buildApprovedCourseEmailForInstructor(String title, String instructorName) {
    return buildEmail(
        "Khóa học của bạn đã được phê duyệt",
        instructorName,
        "Chúc mừng! Khóa học "
            + title
            + " của bạn đã được phê duyệt và sẵn sàng để học viên đăng ký. "
            + "\nHãy truy cập vào hệ thống để xem chi tiết.");
  }

  public String buildApprovedCourseEmailForStudent(
      String title, String instructorName, String studentName) {
    return buildEmail(
        "Khóa học mới từ giảng viên bạn theo dõi",
        studentName,
        "Giảng viên "
            + instructorName
            + " vừa ra mắt khóa học mới: "
            + title
            + "\nHãy ghé thăm để tìm hiểu thêm!");
  }

  @Override
  public String buildRejectCourseEmail(String title, String name, String reason) {
    return buildEmail(
        "Khóa học của bạn đã bị từ chối",
        name,
        "Khóa học "
            + title
            + " của bạn đã bị từ chối với lý do: "
            + reason
            + "\nVui lòng chỉnh sửa và gửi lại để được xem xét.");
  }
}
