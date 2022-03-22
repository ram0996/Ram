package com.affirm.payment.service.executor.request;

import de.hybris.platform.payment.enums.PaymentTransactionType;

import java.util.HashMap;
import java.util.Map;

public class AffirmPaymentServiceRequest {

   private PaymentTransactionType paymentTransactionType;
   private final Map<String, Object> params = new HashMap<>();

   public AffirmPaymentServiceRequest transactionType(PaymentTransactionType transactionType){
      this.paymentTransactionType = transactionType;
      return this;
   }

   /**
    * Adds payment request parameter to the payment service.
    *
    * @param name the name of parameter
    * @param value the value of parameter
    * @return this
    */
   public AffirmPaymentServiceRequest addParam(final String name, final Object value)
   {
      params.put(name, value);
      return this;
   }

   public Map<String, Object> getParams() {
      return params;
   }

   public static AffirmPaymentServiceRequest create() {
      return new AffirmPaymentServiceRequest();
   }

   public PaymentTransactionType getPaymentTransactionType() {
      return paymentTransactionType;
   }

   public void setPaymentTransactionType(PaymentTransactionType paymentTransactionType) {
      this.paymentTransactionType = paymentTransactionType;
   }
}
