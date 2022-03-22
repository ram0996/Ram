package com.affirm.payment.service.executor.response;

import com.affirm.payment.core.service.AffirmPaymentTransactionStrategy;
import com.affirm.payment.model.AffirmPaymentTransactionEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.servicelayer.model.ModelService;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Resource;

public class AffirmProperyPresentPaymentResponseStrategy implements AffirmPaymentResponseStrategy {

   private String property;

   @Resource private AffirmPaymentTransactionStrategy affirmPaymentTransactionStrategy;
   @Resource private ModelService modelService;

   @Override public void validateTransactionResult(AbstractOrderModel abstractOrderModel, AffirmPaymentTransactionEntryModel transactionEntry) {
      transactionEntry.setTransactionStatus(TransactionStatus.ERROR.name());
      if(transactionEntry.getProperties() == null){
         return;
      }

      String transactionId = transactionEntry.getProperties().get(property);
      if(transactionId != null){
         transactionEntry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
      }else {
         transactionEntry.setTransactionStatus(TransactionStatus.REJECTED.name());
      }
   }

   public String getProperty() {
      return property;
   }

   @Required
   public void setProperty(String property) {
      this.property = property;
   }
}
