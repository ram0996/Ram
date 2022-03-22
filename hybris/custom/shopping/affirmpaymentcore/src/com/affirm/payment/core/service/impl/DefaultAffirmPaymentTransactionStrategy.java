package com.affirm.payment.core.service.impl;

import com.affirm.payment.AffirmPaymentConstants;
import com.affirm.payment.core.service.AffirmPaymentTransactionStrategy;
import com.affirm.payment.exception.AffirmPaymentException;
import com.affirm.payment.model.AffirmPaymentTransactionEntryModel;
import com.google.common.collect.Lists;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DefaultAffirmPaymentTransactionStrategy implements AffirmPaymentTransactionStrategy {

   protected ModelService modelService;

   @Override public <T extends PaymentInfoModel> T createPaymentInfo(Class<T> paymentInfoModelClass, PaymentModeModel affirmPaymentMode, AbstractOrderModel cart) {
      T paymentInfo = modelService.create(paymentInfoModelClass);
      paymentInfo.setCode(affirmPaymentMode.getCode() + "_" + cart.getCode());
      paymentInfo.setUser(cart.getUser());
      return paymentInfo;
   }

   @Override public PaymentTransactionModel getTransaction(AbstractOrderModel cart) {
      String paymentMode = cart.getPaymentMode().getCode();
      if(CollectionUtils.isNotEmpty(cart.getPaymentTransactions())){
         Optional<PaymentTransactionModel> optional = cart.getPaymentTransactions().stream().filter(p -> paymentMode.equalsIgnoreCase(p.getPaymentProvider())).findAny();
         if(optional.isPresent()){
            return optional.get();
         }
      }

      return null;
   }

   @Override public PaymentTransactionModel getOrCreateTransaction(AbstractOrderModel cart) {
      String paymentMode = cart.getPaymentMode().getCode();
      PaymentTransactionModel transaction = getTransaction(cart);

      if(transaction == null) {
         ArrayList<PaymentTransactionModel> transactions = new ArrayList<>();
         if(CollectionUtils.isNotEmpty(cart.getPaymentTransactions())){
            transactions.addAll(cart.getPaymentTransactions());
         }
         transaction = modelService.create(PaymentTransactionModel.class);
         transaction.setCode(cart.getUser().getUid() + "_" + UUID.randomUUID());
         transaction.setRequestId(cart.getCode());
         transaction.setRequestToken(cart.getCode());
         transaction.setPaymentProvider(paymentMode);
         transactions.add(transaction);
         cart.setPaymentTransactions(transactions);
         modelService.saveAll(transaction, cart);
      }
      return transaction;
   }

   @Override public <T extends PaymentTransactionEntryModel> T createTransactionEntryModel(Class<T> transactionEntryClass, String requestId, PaymentTransactionType transactionType,
         PaymentTransactionModel transaction) {
      final T entry = modelService.create(transactionEntryClass);
      entry.setType(transactionType);
      entry.setRequestId(requestId);
      entry.setRequestToken(requestId);
      entry.setTime(new Date());
      entry.setPaymentTransaction(transaction);
      entry.setTransactionStatus(TransactionStatus.ACCEPTED.name());
      entry.setTransactionStatusDetails(TransactionStatusDetails.SUCCESFULL.name());
      entry.setCode(getNewPaymentTransactionEntryCode(transaction, transactionType));
      modelService.save(entry);
      modelService.refresh(transaction);
      return entry;

   }

   @Override public Optional<PaymentTransactionEntryModel> getTransactionEntry(PaymentTransactionModel affirmTransaction, PaymentTransactionType transactionType,
         TransactionStatus ... statuses) {
      final List<PaymentTransactionEntryModel> reversedTransactionEntries = Lists.newArrayList(affirmTransaction.getEntries());

      Collections.reverse(reversedTransactionEntries);

      return reversedTransactionEntries.stream()
            .filter(e -> transactionType.equals(e.getType())
                  && Arrays.stream(statuses).anyMatch(s -> s.name().equals(e.getTransactionStatus())))
            .findFirst();
   }

   @Override public BigDecimal getRefundedAmount(OrderModel order) {
      BigDecimal refundedAmount = BigDecimal.ZERO;

      PaymentTransactionModel transactionModel = getTransaction(order);
      if(transactionModel != null){
         Predicate<PaymentTransactionEntryModel> accepted = entry -> TransactionStatus.ACCEPTED.name().equals(entry.getTransactionStatus());
         Predicate<PaymentTransactionEntryModel> refund = entry -> PaymentTransactionType.REFUND_STANDALONE.equals(entry.getType());

         List<PaymentTransactionEntryModel> entries = transactionModel.getEntries().stream().filter(accepted).filter(refund).collect(Collectors.toList());
         if(CollectionUtils.isNotEmpty(entries)){
            for (PaymentTransactionEntryModel entry: entries) {
               if(!(entry instanceof AffirmPaymentTransactionEntryModel)){
                  continue;
               }
               AffirmPaymentTransactionEntryModel affirmEntry = (AffirmPaymentTransactionEntryModel) entry;
               String amount = affirmEntry.getProperties().get("amount");
               if(amount != null) {
                  refundedAmount = refundedAmount.add(BigDecimal.valueOf(Double.valueOf(amount)));
               }
            }
         }
      }
      return refundedAmount;
   }

   public AffirmPaymentTransactionEntryModel getAcceptedTransaction(AbstractOrderModel order, PaymentTransactionType transactionType) {
      if(order == null){
         throw new AffirmPaymentException(AffirmPaymentConstants.REASON_CODE.INVALID_DATA, "order is not found with the given id");
      }
      PaymentTransactionModel affirmTransaction = getTransaction(order);
      if (affirmTransaction == null){
         throw new AffirmPaymentException(AffirmPaymentConstants.REASON_CODE.INVALID_DATA, "no payment information found");
      }

      Optional<PaymentTransactionEntryModel> entry = getTransactionEntry(affirmTransaction, transactionType, TransactionStatus.ACCEPTED);
      if(!entry.isPresent()){
         throw new AffirmPaymentException(AffirmPaymentConstants.REASON_CODE.INVALID_DATA, "no authorisation transaction found");
      }

      PaymentTransactionEntryModel entryModel = entry.get();
      if(!isTransactionSuccessfull(entryModel)){
         throw new AffirmPaymentException(AffirmPaymentConstants.REASON_CODE.INVALID_DATA, "no accepted authorisation transaction found");
      }
      return (AffirmPaymentTransactionEntryModel)entryModel;
   }

   @Override public AffirmPaymentTransactionEntryModel getAcceptedTransactionSlient(AbstractOrderModel order, PaymentTransactionType transactionType) {
      if(order == null){
         return null;
      }
      PaymentTransactionModel affirmTransaction = getTransaction(order);
      if (affirmTransaction == null){
         return null;
      }

      Optional<PaymentTransactionEntryModel> entry = getTransactionEntry(affirmTransaction, transactionType, TransactionStatus.ACCEPTED);
      if(!entry.isPresent()){
         return null;
      }

      PaymentTransactionEntryModel entryModel = entry.get();
      if(!isTransactionSuccessfull(entryModel)){
         return null;
      }
      return (AffirmPaymentTransactionEntryModel)entryModel;
   }

   public boolean isTransactionSuccessfull(PaymentTransactionEntryModel transaction) {
      return TransactionStatus.ACCEPTED.name().equals(transaction != null ? transaction.getTransactionStatus() : null);
   }

   public String getNewPaymentTransactionEntryCode(PaymentTransactionModel transaction, PaymentTransactionType paymentTransactionType) {
      return transaction.getEntries() == null ? transaction.getCode() + "-" + paymentTransactionType.getCode() + "-1" : transaction.getCode() + "-" + paymentTransactionType.getCode() + "-" + (transaction.getEntries().size() + 1);
   }

   public void setModelService(ModelService modelService) {
      this.modelService = modelService;
   }
}
