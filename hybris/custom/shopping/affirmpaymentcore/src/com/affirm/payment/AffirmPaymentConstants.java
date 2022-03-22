package com.affirm.payment;

public interface AffirmPaymentConstants {

   public static final String ORDER = "order";
   public static final String ORDER_ID = "order_id" ;
   public static final String CHARGE_ID = "chargeId";
   public static final String TRANSACTION = "transaction";
   public static final String REQUEST_ID = "request_id";

   public class REASON_CODE {
      public static final String INVALID_DATA = "150_INVALID_DATA" ;
   }

   public class Authorisation {
      public static final String CHECKOUT_TOKEN = "checkout_token" ;
   }

   public class REFUND {
      public static final String AMOUNT = "amount" ;
   }

   public class UPDATE {
      public static final String REQUEST = "update_request" ;

      public static final Object ORDER_ID = "order_id" ;
      public static final Object SHIPPING_CARRIER = "shipping_carrier" ;
      public static final Object SHIPPING_CONFIRMATION = "shipping_confirmation" ;
      public static final Object SHIPPING = "shipping" ;
   }
}
