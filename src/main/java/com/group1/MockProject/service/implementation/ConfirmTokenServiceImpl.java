package com.group1.MockProject.service.implementation;

import com.group1.MockProject.entity.ConfirmToken;
import com.group1.MockProject.repository.ConfirmTokenRepository;
import com.group1.MockProject.service.ConfirmTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmTokenServiceImpl implements ConfirmTokenService {

    ConfirmTokenRepository confirmTokenRepository;

    @Override
    public void saveConfirmToken(ConfirmToken token) {
        confirmTokenRepository.save(token);
    }

    public Optional<ConfirmToken> getToken(String token) {
        return confirmTokenRepository.findByToken(token);
    }

    public int setConfirmedAt(String token) {
        return confirmTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
    }
}
