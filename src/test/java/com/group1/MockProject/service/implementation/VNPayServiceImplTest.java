package com.group1.MockProject.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import com.group1.MockProject.config.VNPAYConfig;
import com.group1.MockProject.dto.PaymentDTO;
import com.group1.MockProject.entity.Payment;
import com.group1.MockProject.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VNPayServiceImplTest {

  @Mock private VNPAYConfig vnPayConfig;

  @Mock private HttpServletRequest request;

  @InjectMocks private VNPayServiceImpl vnPayService;

  private MockedStatic<VNPayUtil> VNPayUtilMock;

  @BeforeEach
  void setUp() {
    VNPayUtilMock = Mockito.mockStatic(VNPayUtil.class);
    VNPayUtilMock.when(() -> VNPayUtil.getIpAddress(request)).thenReturn("127.0.0.1");

    VNPayUtilMock.when(() -> VNPayUtil.getPaymentURL(Mockito.anyMap(), Mockito.eq(true)))
        .thenReturn("mockQueryUrl");
    VNPayUtilMock.when(() -> VNPayUtil.getPaymentURL(Mockito.anyMap(), Mockito.eq(false)))
        .thenReturn("mockHashData");
    VNPayUtilMock.when(() -> VNPayUtil.hmacSHA512(Mockito.anyString(), Mockito.anyString()))
        .thenReturn("mockSecureHash");
  }

  @AfterEach
  void tearDown() {
    if (VNPayUtilMock != null) {
      VNPayUtilMock.close();
    }
  }

  @Test
  public void testCreateVnPayPayment_Success() {
    // Arrange
    Payment payment = new Payment();
    payment.setId(1);
    payment.setTotal_price(1000.0);

    // Mock VNPayConfig methods
    Map<String, String> mockConfig = new HashMap<>();
    mockConfig.put("vnp_Version", "2.1.0");
    mockConfig.put("vnp_Command", "pay");
    Mockito.when(vnPayConfig.getVNPayConfig()).thenReturn(mockConfig);
    Mockito.when(vnPayConfig.getSecretKey()).thenReturn("mockSecretKey");
    Mockito.when(vnPayConfig.getVnp_PayUrl()).thenReturn("https://mockpayment.url");

    PaymentDTO.VNPayResponse response = vnPayService.createVnPayPayment(payment, request);

    Assertions.assertNotNull(response);
    System.out.println(response);
  }
}
