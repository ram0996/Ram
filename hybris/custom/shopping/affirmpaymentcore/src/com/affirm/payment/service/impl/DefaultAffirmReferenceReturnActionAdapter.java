package com.affirm.payment.service.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.returns.impl.DefaultReturnActionAdapter;
import de.hybris.platform.returns.model.ReturnRequestModel;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.affirm.payment.RefundUtil;
import com.affirm.payment.core.service.AffirmPaymentCoreService;
import com.affirm.payment.service.AffirmPaymentService;

public class DefaultAffirmReferenceReturnActionAdapter extends DefaultReturnActionAdapter
{

	private static final Logger LOG = Logger.getLogger(DefaultAffirmReferenceReturnActionAdapter.class.getName());
	@Resource
	AffirmPaymentService affirmPaymentService;
	@Resource
	AffirmPaymentCoreService affirmPaymentCoreService;

	@Override
	public void requestManualPaymentReversalForReturnRequest(ReturnRequestModel returnRequest)
	{
		OrderModel order = returnRequest.getOrder();
		if (affirmPaymentCoreService.isAffirmOrder(order) && !affirmPaymentCoreService.isVCNOrder(order))
		{
			LOG.info("Order placed with Affirm so make a full refund");
			affirmPaymentService.refundOrder(order, RefundUtil.getOrderRefundAmount(returnRequest));
		}
		else
		{
			super.requestManualPaymentReversalForReturnRequest(returnRequest);
		}
	}
}
