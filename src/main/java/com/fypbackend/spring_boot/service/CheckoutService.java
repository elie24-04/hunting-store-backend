package com.fypbackend.spring_boot.service;

import com.fypbackend.spring_boot.dto.Purchase;
import com.fypbackend.spring_boot.dto.PurchaseResponse;
import com.fypbackend.spring_boot.dto.auth.PaymentInfo;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface CheckoutService {

    PurchaseResponse placeOrder(Purchase purchase);

    PaymentIntent createPaymentIntent(PaymentInfo paymentInfo) throws StripeException;
}
