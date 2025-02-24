package com.group1.MockProject.service.implementation;

import com.group1.MockProject.config.Config;
import com.group1.MockProject.dto.ApiResponseDto;
import com.group1.MockProject.dto.request.AddPaymentRequest;
import com.group1.MockProject.dto.request.PaymentRequest;
import com.group1.MockProject.dto.response.AddPaymentResponse;
import com.group1.MockProject.dto.response.PaymentDTO;
import com.group1.MockProject.dto.response.PaymentResponse;
import com.group1.MockProject.entity.*;
import com.group1.MockProject.repository.*;
import com.group1.MockProject.service.PaymentService;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

//import com.group1.MockProject.utils.GetAuthUserInfo;
import com.group1.MockProject.service.SavedCourseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.eclipse.angus.mail.iap.Response;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {
  private final PaymentRepository paymentRepository;
  private final CourseRepository courseRepository;
  private final PaymentDetailRepository paymentDetailRepository;
  private final SavedCourseRepository savedCourseRepository;
  private final StudentRepository studentRepository;
  private final UserRepository userRepository;
  private final SavedCourseService savedCourseService;
  private final MyCourseRepository myCourseRepository;


  @Override
  public ResponseEntity<?> createPayment(int idCourse) throws UnsupportedEncodingException {
    Course course =
        courseRepository.findById(idCourse).orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khóa học",1));
    long amount =  (long) (course.getPrice()*100);
    Payment payment = new Payment();
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String email = auth.getName();
    User user = userRepository.findByEmail(email).get();
    int idStudent = user.getStudent().getId();
    payment.setTotal_price(amount);
    payment.setStatus(0);
    payment.setPayment_date(LocalDateTime.now());
    payment.setStudent(studentRepository.findById(idStudent).get());
    paymentRepository.save(payment);

    PaymentDetail paymentDetail = new PaymentDetail();
    paymentDetail.setPayment(payment);
    paymentDetail.setCourse(course);
    paymentDetailRepository.save(paymentDetail);

    String vnp_IpAddr = "127.0.0.1";
    Map<String, String> vnp_Params = new HashMap<>();
    vnp_Params.put("vnp_Version", Config.vnp_Version);
    vnp_Params.put("vnp_Command", Config.vnp_Command);
    vnp_Params.put("vnp_TmnCode", Config.vnp_TmnCode);
    vnp_Params.put("vnp_BankCode", "NCB");
    vnp_Params.put("vnp_Locale", "vn");
    vnp_Params.put("vnp_Amount", String.valueOf(amount));
    vnp_Params.put("vnp_CurrCode", "VND");
    vnp_Params.put("vnp_TxnRef", payment.getPaymentCode());
    vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + payment.getPaymentCode());
    vnp_Params.put("vnp_OrderType", Config.orderType);
    vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
    vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    String vnp_CreateDate = formatter.format(cld.getTime());
    vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
    cld.add(Calendar.MINUTE, 15);
    String vnp_ExpireDate = formatter.format(cld.getTime());
    vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

    List fieldNames = new ArrayList(vnp_Params.keySet());
    Collections.sort(fieldNames);
    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();
    Iterator itr = fieldNames.iterator();
    while (itr.hasNext()) {
      String fieldName = (String) itr.next();
      String fieldValue = (String) vnp_Params.get(fieldName);
      if ((fieldValue != null) && (fieldValue.length() > 0)) {
        //Build hash data
        hashData.append(fieldName);
        hashData.append('=');
        hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
        //Build query
        query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
        query.append('=');
        query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
        if (itr.hasNext()) {
          query.append('&');
          hashData.append('&');
        }
      }
    }


    String queryUrl = query.toString();
    String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
    queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
    String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
    PaymentDTO paymentDTO = new PaymentDTO();
    paymentDTO.setStatus("OK");
    paymentDTO.setMessage("Success");
    paymentDTO.setUrl(paymentUrl);


    return ResponseEntity.status(HttpStatus.OK).body(paymentDTO);
  }

  @Override
  public ResponseEntity<ApiResponseDto<Payment>> handleVnPayReturn(Map<String, String> allParams) {
    String vnp_TxnRef = allParams.get("vnp_TxnRef");
    String vnp_ResponseCode = allParams.get("vnp_ResponseCode");

    // Kiểm tra mã phản hồi từ VNPay để xác định giao dịch thành công hay thất bại
    if ("00".equals(vnp_ResponseCode)) {
      // Giao dịch thành công
      Payment payment = paymentRepository.findByPaymentCode(vnp_TxnRef).orElseThrow(()->
              new EmptyResultDataAccessException("Không tìm thấy hóa đơn", 1)
      );
      if (payment != null) {
        payment.getPaymentDetails().forEach(paymentDetail -> {
          Course course= paymentDetail.getCourse();
          MyCourse myCourse = new MyCourse();
          myCourse.setStudent(paymentDetail.getStudent());
          myCourse.setCourse(course);
          myCourseRepository.save(myCourse);
        });
        payment.setStatus(1);
        paymentRepository.save(payment);
//        logger.info("Payment with TxnRef {} completed successfully", vnp_TxnRef);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponseDto.<Payment>builder()
                        .status(200)
                        .message(HttpStatus.OK.getReasonPhrase())
                        .build()
        );
      } else {
//        logger.error("Payment with TxnRef {} not found", vnp_TxnRef);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseDto.<Payment>builder()
                        .status(404)
                        .message(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .build()
                );
      }
    } else {
      // Giao dịch thất bại
//      logger.error("Payment with TxnRef {} failed with response code {}", vnp_TxnRef, vnp_ResponseCode);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
              ApiResponseDto.<Payment>builder()
                      .status(400)
                      .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                      .build()
      );
    }
  }
  @Override
  public AddPaymentResponse addPaymentDetail(String email, AddPaymentRequest request) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new EmptyResultDataAccessException(
                        "Người dùng không tồn tại trong hệ thống", 1));
    if(user.getStatus()==0){
      throw new AccessDeniedException("Tài khoản của bạn chưa được kích hoạt");
    }
    Student student =
        studentRepository
            .findByUser(user)
            .orElseThrow(() -> new AccessDeniedException("Bạn không có quyền đăng kí khóa học"));

    Course course =
        courseRepository
            .findById(request.getCourseId())
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy khóa học", 1));
    if(course.getStatus()==0){
      throw new AccessDeniedException("Khóa học chưa đươc duyệt");
    }
    // Get unpaid payment
    Payment payment =
        paymentRepository
            .findByStatus(1)
            .orElseGet(
                () -> {
                  Payment newPayment = new Payment();
                  newPayment.setStudent(student);
                  newPayment.setStatus(1);
                  newPayment.setTotal_price(0L);
                  newPayment.setPaymentCode(generatePaymentCode());
                  return paymentRepository.save(newPayment);
                });

    Optional<MyCourse> savedCourseOptional =
        myCourseRepository.findByCourseAndStudent(course, student);

    if (savedCourseOptional.isPresent()) {
      throw new DataIntegrityViolationException("Khóa học đã được mua");
    }

    Optional<PaymentDetail> paymentDetailOptional =
        paymentDetailRepository.findByPaymentAndCourse(payment, course);

    if (paymentDetailOptional.isPresent()) {
      throw new DataIntegrityViolationException("Đã có khóa học trong hóa đơn");
    }

      PaymentDetail newPaymentDetail = new PaymentDetail();
      payment.setTotal_price(payment.getTotal_price() + course.getPrice().longValue());
      payment = paymentRepository.save(payment);
      newPaymentDetail.setPayment(payment);
      newPaymentDetail.setCourse(course);
      newPaymentDetail.setStudent(student);
      paymentDetailRepository.save(newPaymentDetail);

    return new AddPaymentResponse("Thêm khóa học vào hóa đơn thành công");
  }

  @Override
  public Payment checkPayment(String email, PaymentRequest request) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new EmptyResultDataAccessException(
                        "Người dùng không tồn tại trong hệ thống", 1));
    if(user.getStatus()==0){
      throw new AccessDeniedException("Tài khoản của bạn chưa được kích hoạt");
    }
    studentRepository
        .findByUser(user)
        .orElseThrow(() -> new AccessDeniedException("Bạn không có quyền thanh toán khóa học"));

    Payment payment =
        paymentRepository
            .findById(request.getPaymentId())
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy hóa đơn", 1));
    if(!payment.getStudent().equals(user.getStudent()))
    {
      throw new AccessDeniedException("Bạn không thể thanh toán hóa đơn của người khác");
    }
    if (payment.getStatus() == 0) {
      throw new DataIntegrityViolationException("Hóa đơn đã được thanh toán");
    }
    return payment;
  }

  @Override
  public PaymentResponse callbackPayment(String vnp_TxnRef) {
    String paymentCode = vnp_TxnRef;
    Payment payment =
        paymentRepository
            .findByPaymentCode(paymentCode)
            .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy hóa đơn", 1));

    List<PaymentDetail> paymentDetails = paymentDetailRepository.findByPayment(payment);
    payment.setStatus(0);
    payment.setPayment_date(LocalDateTime.now());
    paymentRepository.save(payment);

    for (PaymentDetail paymentDetail : paymentDetails) {
      MyCourse myCourse = new MyCourse();
      myCourse.setStudent(payment.getStudent());
      myCourse.setCourse(paymentDetail.getCourse());
      myCourseRepository.save(myCourse);
    }
    return new PaymentResponse("Thanh toán thành công");
  }
  @Override
  public PaymentResponse freePayment(Payment payment) {
    List<PaymentDetail> paymentDetails = paymentDetailRepository.findByPayment(payment);
    payment.setStatus(0);
    payment.setPayment_date(LocalDateTime.now());
    paymentRepository.save(payment);
    for (PaymentDetail paymentDetail : paymentDetails) {
      MyCourse myCourse = new MyCourse();
      myCourse.setStudent(payment.getStudent());
      myCourse.setCourse(paymentDetail.getCourse());
      myCourseRepository.save(myCourse);
    }
    return new PaymentResponse("Thanh toán thành công");
  }
  @Override
  public PaymentResponse callbackPaymentFail(String vnp_TxnRef) {
    String paymentCode = vnp_TxnRef;
    Payment payment =
            paymentRepository
                    .findByPaymentCode(paymentCode)
                    .orElseThrow(() -> new EmptyResultDataAccessException("Không tìm thấy hóa đơn", 1));
    paymentRepository.delete(payment);
    return new PaymentResponse("Thanh toán thất bại");
  }
  @Override
  public ResponseEntity<?> paymentSavedCourse(String email) throws UnsupportedEncodingException {
    Optional<User> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      throw new IllegalArgumentException("Người dùng không tồn tại trong hệ thống");
    }
    Optional<Student> student = studentRepository.findByUser(user.get());
    if (student.isEmpty()) {
      throw new AccessDeniedException("Bạn không có quyền thanh toán khóa học");
    }
    List<SavedCourse> savedCourses = savedCourseService.getSavedCoursesByEmail(email).getSavedCourse();
    if (savedCourses.isEmpty()) {
      throw new IllegalArgumentException("Bạn chưa có danh sách khóa học cần thanh toán");
    }
    List<Course> courses = new ArrayList<>();
    double amount = 0;
    for (SavedCourse savedCourse : savedCourses) {
      amount += savedCourse.getCourse().getPrice();
      courses.add(savedCourse.getCourse());
    }
    amount *= 100;
    String vnp_TxnRef = Config.getRandomNumber(8);
    String vnp_IpAddr = "127.0.0.1";

    Map<String, String> vnp_Params = new HashMap<>();
    vnp_Params.put("vnp_Version", Config.vnp_Version);
    vnp_Params.put("vnp_Command", Config.vnp_Command);
    vnp_Params.put("vnp_TmnCode", Config.vnp_TmnCode);
    vnp_Params.put("vnp_BankCode", "NCB");
    vnp_Params.put("vnp_Locale", "vn");
    vnp_Params.put("vnp_Amount", String.valueOf(amount));
    vnp_Params.put("vnp_CurrCode", "VND");
    vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
    vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
    vnp_Params.put("vnp_OrderType", Config.orderType);
    vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
    vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    String vnp_CreateDate = formatter.format(cld.getTime());
    vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
    cld.add(Calendar.MINUTE, 15);
    String vnp_ExpireDate = formatter.format(cld.getTime());
    vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

    List fieldNames = new ArrayList(vnp_Params.keySet());
    Collections.sort(fieldNames);
    StringBuilder hashData = new StringBuilder();
    StringBuilder query = new StringBuilder();
    Iterator itr = fieldNames.iterator();
    while (itr.hasNext()) {
      String fieldName = (String) itr.next();
      String fieldValue = (String) vnp_Params.get(fieldName);
      if ((fieldValue != null) && (fieldValue.length() > 0)) {
        //Build hash data
        hashData.append(fieldName);
        hashData.append('=');
        hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
        //Build query
        query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
        query.append('=');
        query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
        if (itr.hasNext()) {
          query.append('&');
          hashData.append('&');
        }
      }
    }
    String queryUrl = query.toString();
    String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
    queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
    String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;
    PaymentDTO paymentDTO = new PaymentDTO();
    paymentDTO.setStatus("OK");
    paymentDTO.setMessage("Success");
    paymentDTO.setUrl(paymentUrl);

    int idStudent = user.get().getStudent().getId();
    Payment payment = new Payment();
    payment.setStudent(student.get());
    payment.setTotal_price((long)amount);
    payment.setPaymentCode(generatePaymentCode());
    payment.setStudent(studentRepository.findById(idStudent).get());
    payment.setPayment_date(LocalDateTime.now());
    paymentRepository.save(payment);
    for(Course course: courses){
      PaymentDetail paymentDetail = new PaymentDetail();
      paymentDetail.setPayment(payment);
      paymentDetail.setPayment_date(LocalDate.now());
      paymentDetail.setCourse(course);
      paymentDetailRepository.save(paymentDetail);
    }


    return ResponseEntity.status(HttpStatus.OK).body(paymentDTO);
  }
  public String generatePaymentCode() {
    String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
    String randomPart = generateRandomString(4);
    return "PM-" + datePart + "-" + randomPart;
  }

  public String generateRandomString(int length) {
    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    StringBuilder result = new StringBuilder();
    Random random = new Random();
    for (int i = 0; i < length; i++) {
      result.append(characters.charAt(random.nextInt(characters.length())));
    }
    return result.toString();
  }
}



