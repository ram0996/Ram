package com.affirm.payment.service.executor.request.validator;

import com.affirm.payment.service.executor.request.AffirmPaymentServiceRequest;

public interface PaymentRequestValidator {

   boolean isValid(AffirmPaymentServiceRequest request);

}
