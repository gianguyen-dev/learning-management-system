package com.group1.MockProject.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class PaymentDTO implements Serializable {
    private String status;
    private String message;
    private String url;
}
