package com.affirm.payment.service.executor.request.builder;

import com.affirm.payment.AffirmPaymentConstants;

import java.math.BigDecimal;

public class AffirmRefundPaymentRequestServiceBuilder extends AffirmPaymentRequestServiceBuilder<AffirmRefundPaymentRequestServiceBuilder> {

   public AffirmRefundPaymentRequestServiceBuilder setRefundAmount(final BigDecimal amount)
   {
      affirmPaymentServiceRequest.addParam(AffirmPaymentConstants.REFUND.AMOUNT, amount);
      return this;
   }

}
