package com.group1.MockProject.service.implementation;

import com.group1.MockProject.entity.ConfirmToken;
import com.group1.MockProject.repository.ConfirmTokenRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfirmTokenServiceImplTest {
  @Mock private ConfirmTokenRepository confirmTokenRepository;

  @InjectMocks private ConfirmTokenServiceImpl confirmTokenService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void testSaveConfirmToken_Success() {
    String mockToken = "mockToken";

    ConfirmToken confirmToken = new ConfirmToken();
    confirmToken.setToken(mockToken);
    confirmTokenService.saveConfirmToken(confirmToken);

    Mockito.verify(confirmTokenRepository, Mockito.times(1)).save(confirmToken);
  }

  @Test
  public void testGetToken_Success() {
    String mockToken = "mockToken";
    ConfirmToken confirmToken = new ConfirmToken();

    Mockito.when(confirmTokenRepository.findByToken(mockToken))
        .thenReturn(Optional.of(confirmToken));

    Optional<ConfirmToken> result = confirmTokenService.getToken(mockToken);

    Assertions.assertTrue(result.isPresent());
    Assertions.assertEquals(confirmToken.getToken(), result.get().getToken());
    Mockito.verify(confirmTokenRepository, Mockito.times(1)).findByToken(mockToken);
  }

  @Test
  public void testSetConfirmedAt_Success() {
    String token = "mockToken";
    int expectedUpdateCount = 1;

    Mockito.when(
            confirmTokenRepository.updateConfirmedAt(
                Mockito.eq(token), Mockito.any(LocalDateTime.class)))
        .thenReturn(expectedUpdateCount);

    int result = confirmTokenService.setConfirmedAt(token);

    Assertions.assertEquals(expectedUpdateCount, result);
    Mockito.verify(confirmTokenRepository)
        .updateConfirmedAt(
            Mockito.eq(token), Mockito.any(LocalDateTime.class)); // Verify repository call
  }
}
