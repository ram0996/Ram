package com.affirm.payment.service.executor.request.builder;

import com.affirm.payment.AffirmPaymentConstants;

public class AffirmAuthorisationPaymentRequestServiceBuilder extends AffirmPaymentRequestServiceBuilder<AffirmAuthorisationPaymentRequestServiceBuilder> {

   public AffirmAuthorisationPaymentRequestServiceBuilder  setCheckoutToken(final String checkoutToken)
   {
      affirmPaymentServiceRequest.addParam(AffirmPaymentConstants.Authorisation.CHECKOUT_TOKEN, checkoutToken);
      return this;
   }

}
