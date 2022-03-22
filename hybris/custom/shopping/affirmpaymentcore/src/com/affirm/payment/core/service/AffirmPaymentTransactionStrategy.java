package com.affirm.payment.core.service;

import com.affirm.payment.model.AffirmPaymentTransactionEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;

import java.math.BigDecimal;
import java.util.Optional;

public interface AffirmPaymentTransactionStrategy {

   <T extends PaymentInfoModel> T createPaymentInfo(Class<T> paymentInfoModelClass, PaymentModeModel affirmPaymentMode, AbstractOrderModel cart);

   PaymentTransactionModel getOrCreateTransaction(AbstractOrderModel cart);

   PaymentTransactionModel getTransaction(AbstractOrderModel cart);

   <T extends PaymentTransactionEntryModel> T createTransactionEntryModel(Class<T> transactionEntryClass, String checkoutToken, PaymentTransactionType paymentTransactionType, PaymentTransactionModel transaction);

   Optional<PaymentTransactionEntryModel> getTransactionEntry(PaymentTransactionModel affirmTransaction, PaymentTransactionType transactionType,
         TransactionStatus ... statuses);

   BigDecimal getRefundedAmount(OrderModel order);

   /**
    * Get the accepted transaction for the given transaction type. If the trancastion cannot be found, throws   AffirmPaymentException.
    * @param order the order
    * @param transactionType type of the transaction to be returned (AUTHORISATION, CAPTURE etc)
    * @return return the transaction
    * @throws com.affirm.payment.exception.AffirmPaymentException - thows when transaction is not exist or order is null
    */
   AffirmPaymentTransactionEntryModel getAcceptedTransaction(AbstractOrderModel order, PaymentTransactionType transactionType);

   /**
    * Same as the getAcceptedTransaction method. However it returns null when the transaction is not exist.
    * @param order the order
    * @param transactionType type of the transaction to be returned (AUTHORISATION, CAPTURE etc)
    * @return return the transaction or null if not exist
    */
   AffirmPaymentTransactionEntryModel getAcceptedTransactionSlient(AbstractOrderModel order, PaymentTransactionType transactionType);

   boolean isTransactionSuccessfull(PaymentTransactionEntryModel transaction);
}
