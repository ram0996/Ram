package com.affirm.payment.service.executor.response;

import com.affirm.payment.core.service.AffirmPaymentTransactionStrategy;
import com.affirm.payment.model.AffirmPaymentTransactionEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;

public class AffirmRefundPaymentResponseStrategy implements AffirmPaymentResponseStrategy {

   public static final String STATUS = "status";
   public static final String AUTHORIZED = "authorized";

   public static final String AMOUNT = "amount";

   @Resource private AffirmPaymentTransactionStrategy affirmPaymentTransactionStrategy;
   @Resource private ModelService modelService;

   @Override public void validateTransactionResult(AbstractOrderModel abstractOrderModel, AffirmPaymentTransactionEntryModel transactionEntry) {
      transactionEntry.setTransactionStatus(TransactionStatus.ERROR.name());
      if(transactionEntry.getProperties() == null){
         return;
      }

      String amount = transactionEntry.getProperties().get(AMOUNT);

      if(amount != null){
         transactionEntry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
      }else {
         transactionEntry.setTransactionStatus(TransactionStatus.REJECTED.name());
      }
   }


   private boolean validAmount(String amount, AbstractOrderModel cart) {
      if(amount == null){
         return true;
      }

      try {
         BigInteger parsedAmount = BigInteger.valueOf(Long.valueOf(amount));
         BigInteger cartTotal = BigInteger.valueOf(Double.valueOf(cart.getTotalIncludingTax() * 100.0).longValue());

         return parsedAmount.compareTo(cartTotal) == 0;
      }catch (Exception e){
         return false;
      }
   }

}
