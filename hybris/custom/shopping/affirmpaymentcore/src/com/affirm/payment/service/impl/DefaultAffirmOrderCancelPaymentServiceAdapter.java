package com.affirm.payment.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.ordercancel.OrderCancelPaymentServiceAdapter;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.affirm.payment.core.service.AffirmPaymentCoreService;
import com.affirm.payment.core.service.AffirmPaymentTransactionStrategy;
import com.affirm.payment.exception.AffirmPaymentException;
import com.affirm.payment.model.AffirmPaymentTransactionEntryModel;
import com.affirm.payment.service.AffirmPaymentService;

public class DefaultAffirmOrderCancelPaymentServiceAdapter implements OrderCancelPaymentServiceAdapter
{

	@Resource
	private AffirmPaymentService affirmPaymentService;
	@Resource
	private AffirmPaymentCoreService affirmPaymentCoreService;
	@Resource
	private AffirmPaymentTransactionStrategy affirmPaymentTransactionStrategy;

	private static final Logger LOG = Logger.getLogger(DefaultAffirmReferenceReturnActionAdapter.class.getName());

	@Override
	public void recalculateOrderAndModifyPayments(OrderModel orderModel)
	{
		if (affirmPaymentCoreService.isAffirmOrder(orderModel) && !affirmPaymentCoreService.isVCNOrder(orderModel))
		{
			refundOrVoidAffirmPayment(orderModel);
		}
		else
		{
			LOG.warn("Order cancellation: refund is not implemented for the following order: " + orderModel.getCode());
		}
	}

	private void refundOrVoidAffirmPayment(OrderModel orderModel)
	{
		PaymentTransactionModel paymentTransactionModel = affirmPaymentTransactionStrategy.getTransaction(orderModel);
		try
		{
			AffirmPaymentTransactionEntryModel captureTransaction =
					affirmPaymentTransactionStrategy.getAcceptedTransaction(orderModel, PaymentTransactionType.CAPTURE);
			//if capture transaction exist, do
			affirmPaymentService.refundOrder(orderModel, null);
		}
		catch (AffirmPaymentException pe)
		{
			try
			{
				AffirmPaymentTransactionEntryModel authTransaction =
						affirmPaymentTransactionStrategy.getAcceptedTransaction(orderModel, PaymentTransactionType
								.AUTHORIZATION);
				affirmPaymentService.voidPayment(orderModel);
			}
			catch (AffirmPaymentException pe2)
			{
				LOG.info("No payment transaction found so not void or refund required");
			}
		}

	}
}
