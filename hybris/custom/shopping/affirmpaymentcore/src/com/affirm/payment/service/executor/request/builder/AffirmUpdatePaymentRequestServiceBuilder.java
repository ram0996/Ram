package com.affirm.payment.service.executor.request.builder;

import com.affirm.checkout.pojo.AffirmLoanUpdateRequestData;
import com.affirm.payment.AffirmPaymentConstants;

import java.math.BigDecimal;

public class AffirmUpdatePaymentRequestServiceBuilder extends AffirmPaymentRequestServiceBuilder<AffirmUpdatePaymentRequestServiceBuilder> {

   public AffirmUpdatePaymentRequestServiceBuilder setUpdateRequest(final AffirmLoanUpdateRequestData request)
   {
      affirmPaymentServiceRequest.addParam(AffirmPaymentConstants.UPDATE.REQUEST, request);
      return this;
   }

}
