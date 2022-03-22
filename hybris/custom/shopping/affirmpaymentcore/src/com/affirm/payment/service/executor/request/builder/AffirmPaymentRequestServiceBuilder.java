package com.affirm.payment.service.executor.request.builder;

import com.affirm.payment.AffirmPaymentConstants;
import com.affirm.payment.service.executor.request.AffirmPaymentServiceRequest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;

public class AffirmPaymentRequestServiceBuilder<B extends AffirmPaymentRequestServiceBuilder>
{

   protected final AffirmPaymentServiceRequest affirmPaymentServiceRequest = AffirmPaymentServiceRequest.create();

   public B setOrder(final AbstractOrderModel order)
   {
      affirmPaymentServiceRequest.addParam(AffirmPaymentConstants.ORDER, order);
      affirmPaymentServiceRequest.addParam(AffirmPaymentConstants.ORDER_ID, order.getCode());
      return (B) this;
   }

   public B setTransaction(final PaymentTransactionModel transaction)
   {
      affirmPaymentServiceRequest.addParam(AffirmPaymentConstants.TRANSACTION, transaction);
      return (B) this;
   }

   public B setTransactionType(final PaymentTransactionType transactionType)
   {
      affirmPaymentServiceRequest.transactionType(transactionType);
      return (B) this;
   }

   public B setChargeId(final String chargeId)
   {
      affirmPaymentServiceRequest.addParam(AffirmPaymentConstants.CHARGE_ID, chargeId);
      return (B) this;
   }

   public AffirmPaymentServiceRequest build()
   {
      return affirmPaymentServiceRequest;
   }
}
