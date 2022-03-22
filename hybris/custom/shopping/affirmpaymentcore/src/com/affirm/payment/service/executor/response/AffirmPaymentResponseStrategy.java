package com.affirm.payment.service.executor.response;

import com.affirm.payment.model.AffirmPaymentTransactionEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

public interface AffirmPaymentResponseStrategy {

   void validateTransactionResult(AbstractOrderModel cart, AffirmPaymentTransactionEntryModel transactionEntry);

}
