package com.affirm.payment.service.executor.provider;

import com.affirm.payment.service.executor.request.AffirmPaymentServiceRequest;
import com.affirm.payment.service.executor.result.AffirmPaymentServiceResult;

/**
 * Define a payment provider interface to select the correct provider based on the payment request.
 */
public interface PaymentServiceProvider
{
   /**
    * Return true if the specified service request is supported by the current payment service provider.
    *
    * @param request the request to be checked
    * @return returns true if supported
    */
   boolean supports(final AffirmPaymentServiceRequest request);

   /**
    * Execute the payment service
    *
    * @param request the request to be executed
    * @return the result of the operation
    */
   AffirmPaymentServiceResult execute(final AffirmPaymentServiceRequest request);

   /**
    * Return true when the request is valid
    * @param request request to be validated
    * @return true when reques is valid and can be executed, otherwise false
    */
   boolean valid(AffirmPaymentServiceRequest request);
}

