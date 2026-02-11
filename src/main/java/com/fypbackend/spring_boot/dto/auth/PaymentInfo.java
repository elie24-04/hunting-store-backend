package com.fypbackend.spring_boot.dto.auth;

import lombok.Data;

@Data
public class PaymentInfo {

    private int amount;
    private String currency;
    private String receiptEmail;
    

}
