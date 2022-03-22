package com.affirm.payment.service.executor;

import com.affirm.payment.service.executor.request.AffirmPaymentServiceRequest;
import com.affirm.payment.service.executor.result.AffirmPaymentServiceResult;

public interface AffirmPaymentServiceExecutor {
   AffirmPaymentServiceResult execute(final AffirmPaymentServiceRequest request);
}

