package com.affirm.payment;

import de.hybris.platform.returns.model.RefundEntryModel;
import de.hybris.platform.returns.model.ReturnEntryModel;
import de.hybris.platform.returns.model.ReturnRequestModel;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RefundUtil
{

	public static BigDecimal getOrderRefundAmount(ReturnRequestModel returnRequest)
	{
		BigDecimal refundAmount = (BigDecimal) returnRequest.getReturnEntries().stream().map((returnEntry) -> {
			return getRefundEntryAmount(returnEntry);
		}).reduce(BigDecimal.ZERO, BigDecimal::add);
		if (returnRequest.getRefundDeliveryCost() != null && returnRequest.getRefundDeliveryCost())
		{
			refundAmount = refundAmount.add(BigDecimal.valueOf(returnRequest.getOrder().getDeliveryCost()));
		}

		return refundAmount.setScale(returnRequest.getOrder().getCurrency().getDigits(), RoundingMode.FLOOR);
	}

	public static BigDecimal getRefundEntryAmount(ReturnEntryModel returnEntryModel)
	{
		if (returnEntryModel instanceof RefundEntryModel)
		{
			return ((RefundEntryModel) returnEntryModel).getAmount();
		}

		ReturnRequestModel returnRequest = returnEntryModel.getReturnRequest();
		BigDecimal refundEntryAmount = BigDecimal.ZERO;
		if (returnEntryModel instanceof RefundEntryModel)
		{
			RefundEntryModel refundEntry = (RefundEntryModel) returnEntryModel;
			if (refundEntry.getAmount() != null)
			{
				refundEntryAmount = refundEntry.getAmount();
				refundEntryAmount = refundEntryAmount.setScale(returnRequest.getOrder().getCurrency().getDigits(), 5);
			}
		}

		return refundEntryAmount;
	}

}
