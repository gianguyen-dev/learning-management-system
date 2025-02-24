package com.group1.MockProject.service;

import com.group1.MockProject.entity.ConfirmToken;

import java.util.Optional;

public interface ConfirmTokenService {
    void saveConfirmToken(ConfirmToken token);
    Optional<ConfirmToken> getToken(String token);
    int setConfirmedAt(String token);
}
