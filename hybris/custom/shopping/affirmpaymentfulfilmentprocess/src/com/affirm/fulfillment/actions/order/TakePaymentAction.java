/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.affirm.fulfillment.actions.order;

import com.affirm.payment.core.service.AffirmPaymentTransactionStrategy;
import com.affirm.payment.model.AffirmPaymentTransactionEntryModel;
import com.affirm.payment.service.executor.AffirmPaymentServiceExecutor;
import com.affirm.payment.service.executor.request.AffirmPaymentServiceRequest;
import com.affirm.payment.service.executor.request.builder.AffirmPaymentRequestServiceBuilder;
import com.affirm.payment.service.executor.result.AffirmPaymentServiceResult;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.enums.PaymentStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.AffirmPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 * The TakePayment step captures the payment transaction.
 */
public class TakePaymentAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{

   private static final Logger LOG = Logger.getLogger(TakePaymentAction.class);

   public static final String ID = "id";

   private PaymentService paymentService;
   private AffirmPaymentTransactionStrategy affirmPaymentTransactionStrategy;
   private AffirmPaymentServiceExecutor affirmPaymentServiceExecutor;

	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		final OrderModel order = process.getOrder();

		for (final PaymentTransactionModel txn : order.getPaymentTransactions())
		{
			if (txn.getInfo() instanceof CreditCardPaymentInfoModel)
			{
				final PaymentTransactionEntryModel txnEntry = getPaymentService().capture(txn);

				if (TransactionStatus.ACCEPTED.name().equals(txnEntry.getTransactionStatus()))
				{
					if (LOG.isDebugEnabled())
					{
						LOG.debug("The payment transaction has been captured. Order: " + order.getCode() + ". Txn: " + txn.getCode());
					}
					setOrderStatus(order, OrderStatus.PAYMENT_CAPTURED);
				}
				else
				{
					LOG.error("The payment transaction capture has failed. Order: " + order.getCode() + ". Txn: " + txn.getCode());
					setOrderStatus(order, OrderStatus.PAYMENT_NOT_CAPTURED);
					return Transition.NOK;
				}
			}else if(order.getPaymentInfo() instanceof AffirmPaymentInfoModel){
            return handleAffirmPayment(order);

         }
		}
		return Transition.OK;
	}

   private Transition handleAffirmPayment(OrderModel order) {
      try {
         AffirmPaymentTransactionEntryModel captureEntry = affirmPaymentTransactionStrategy.getAcceptedTransactionSlient(order, PaymentTransactionType.CAPTURE);
         //when payment is already captured we update the order status to PAYMENT_CAPTURED
         if(captureEntry != null){
            setOrderStatus(order, OrderStatus.PAYMENT_CAPTURED);
            order.setPaymentStatus(PaymentStatus.PAID);
            return Transition.OK;
         }

         AffirmPaymentTransactionEntryModel authorisation = affirmPaymentTransactionStrategy.getAcceptedTransaction(order, PaymentTransactionType.AUTHORIZATION);

         //otherwise we try to capture the order
         AffirmPaymentServiceRequest request = new AffirmPaymentRequestServiceBuilder<>()
               .setOrder(order)
               .setTransactionType(PaymentTransactionType.CAPTURE)
               .setChargeId(authorisation.getProperties().get(ID))
               .build();

         AffirmPaymentServiceResult paymentServiceResult = affirmPaymentServiceExecutor.execute(request);

         PaymentTransactionEntryModel transaction = paymentServiceResult.getTransaction();
         if(affirmPaymentTransactionStrategy.isTransactionSuccessfull(transaction)){
            setOrderStatus(order, OrderStatus.PAYMENT_CAPTURED);
            order.setPaymentStatus(PaymentStatus.PAID);
            return Transition.OK;
         }else {
            setOrderStatus(order, OrderStatus.PAYMENT_NOT_CAPTURED);
            return Transition.NOK;
         }

      }catch (ModelNotFoundException ne){
         setOrderStatus(order, OrderStatus.PAYMENT_NOT_CAPTURED);
         return Transition.NOK;
       }
   }

   protected PaymentService getPaymentService()
	{
		return paymentService;
	}

	@Required
	public void setPaymentService(final PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}

   @Required
   public void setAffirmPaymentTransactionStrategy(AffirmPaymentTransactionStrategy affirmPaymentTransactionStrategy) {
      this.affirmPaymentTransactionStrategy = affirmPaymentTransactionStrategy;
   }

   @Required
   public void setAffirmPaymentServiceExecutor(AffirmPaymentServiceExecutor affirmPaymentServiceExecutor) {
      this.affirmPaymentServiceExecutor = affirmPaymentServiceExecutor;
   }
}
