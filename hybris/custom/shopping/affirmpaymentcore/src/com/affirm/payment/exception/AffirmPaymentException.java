package com.affirm.payment.exception;

public class AffirmPaymentException extends RuntimeException {

   private String reasonCode;

   public AffirmPaymentException(String reasonCode, String message) {
      super(message);
      this.reasonCode = reasonCode;
   }

   public AffirmPaymentException(String reasonCode, String message, Throwable cause) {
      super(message, cause);
      this.reasonCode = reasonCode;
   }

   public String getReasonCode() {
      return reasonCode;
   }

   public void setReasonCode(String reasonCode) {
      this.reasonCode = reasonCode;
   }
}
