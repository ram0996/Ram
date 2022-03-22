package com.affirm.payment.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.AffirmPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.affirm.checkout.pojo.AffirmLoanUpdateRequestData;
import com.affirm.payment.AffirmPaymentConstants;
import com.affirm.payment.core.service.AffirmPaymentCoreService;
import com.affirm.payment.core.service.AffirmPaymentTransactionStrategy;
import com.affirm.payment.exception.AffirmPaymentException;
import com.affirm.payment.model.AffirmConfigContainerModel;
import com.affirm.payment.model.AffirmPaymentTransactionEntryModel;
import com.affirm.payment.service.AffirmPaymentService;
import com.affirm.payment.service.executor.AffirmPaymentServiceExecutor;
import com.affirm.payment.service.executor.request.AffirmPaymentServiceRequest;
import com.affirm.payment.service.executor.request.builder.AffirmAuthorisationPaymentRequestServiceBuilder;
import com.affirm.payment.service.executor.request.builder.AffirmPaymentRequestServiceBuilder;
import com.affirm.payment.service.executor.request.builder.AffirmRefundPaymentRequestServiceBuilder;
import com.affirm.payment.service.executor.request.builder.AffirmUpdatePaymentRequestServiceBuilder;
import com.affirm.payment.service.executor.result.AffirmPaymentServiceResult;

public class DefaultAffirmPaymentService implements AffirmPaymentService{

   private static final Logger LOG = Logger.getLogger(DefaultAffirmPaymentService.class);
   public static final String ID = "id";

   @Resource private AffirmPaymentCoreService affirmPaymentCoreService;
   @Resource private ModelService modelService;
   @Resource private AffirmPaymentTransactionStrategy affirmPaymentTransactionStrategy;
   @Resource private AffirmPaymentServiceExecutor affirmPaymentServiceExecutor;

   @Override public void initizePayment(CartModel cart, AddressModel addressModel) {
      cart.setPaymentMode(affirmPaymentCoreService.getPaymentMode());
      AffirmPaymentInfoModel paymentInfo = affirmPaymentTransactionStrategy.createPaymentInfo(AffirmPaymentInfoModel.class, cart.getPaymentMode(), cart);
      addressModel.setOwner(paymentInfo);
      paymentInfo.setBillingAddress(addressModel);
      cart.setPaymentInfo(paymentInfo);
      modelService.saveAll(addressModel, paymentInfo, cart);

   }

   @Override public boolean authorisePayment(CartModel cart, String checkoutToken) {
      PaymentTransactionModel initTransaction = affirmPaymentTransactionStrategy.getOrCreateTransaction(cart);
      AffirmPaymentTransactionEntryModel initTransactionEntry = affirmPaymentTransactionStrategy.createTransactionEntryModel(
            AffirmPaymentTransactionEntryModel.class, checkoutToken, PaymentTransactionType.INITIATE, initTransaction);

      cart.setPaymentMode(affirmPaymentCoreService.getPaymentMode());
      modelService.saveAll(cart, initTransaction, initTransactionEntry);

      AffirmPaymentServiceRequest request = new AffirmAuthorisationPaymentRequestServiceBuilder()
            .setOrder(cart)
            .setTransactionType(PaymentTransactionType.AUTHORIZATION)
            .setCheckoutToken(checkoutToken).build();

      AffirmPaymentServiceResult paymentServiceResult = affirmPaymentServiceExecutor.execute(request);

      PaymentTransactionEntryModel transaction = paymentServiceResult.getTransaction();

      AffirmConfigContainerModel config = affirmPaymentCoreService.getPaymentConfiguration(cart);

      boolean transactionSuccessfull = affirmPaymentTransactionStrategy.isTransactionSuccessfull(transaction);
      if(transactionSuccessfull && config.isAuthAndCapture()){
         try {
            LOG.info("capture the payment for the cart " + cart.getCode());
            return capturePayment(cart);
         }catch (RuntimeException re){
            LOG.info("capture transaction is failed", re);
            try {
               LOG.info("void the payment for the cart " + cart.getCode());
               voidPayment(cart);
            }catch (RuntimeException e){
               LOG.info("void transaction is failed", e);
            }
            throw re;
         }
      }

      return transactionSuccessfull;
   }

   @Override public boolean capturePayment(AbstractOrderModel order) {
      AffirmPaymentTransactionEntryModel authorisation = affirmPaymentTransactionStrategy.getAcceptedTransaction(order, PaymentTransactionType.AUTHORIZATION);

      AffirmPaymentServiceRequest request = new AffirmPaymentRequestServiceBuilder()
            .setOrder(order)
            .setTransactionType(PaymentTransactionType.CAPTURE)
            .setChargeId(authorisation.getProperties().get(ID))
            .build();

      AffirmPaymentServiceResult paymentServiceResult = affirmPaymentServiceExecutor.execute(request);

      PaymentTransactionEntryModel transaction = paymentServiceResult.getTransaction();
      return affirmPaymentTransactionStrategy.isTransactionSuccessfull(transaction);
   }

   @Override public boolean voidPayment(AbstractOrderModel order) {
      AffirmPaymentTransactionEntryModel authorisation = affirmPaymentTransactionStrategy.getAcceptedTransaction(order, PaymentTransactionType.AUTHORIZATION);
      AffirmPaymentServiceRequest request = new AffirmPaymentRequestServiceBuilder()
            .setOrder(order)
            .setChargeId(authorisation.getProperties().get(ID))
            .setTransactionType(PaymentTransactionType.CANCEL)
            .build();

      AffirmPaymentServiceResult paymentServiceResult = affirmPaymentServiceExecutor.execute(request);

      PaymentTransactionEntryModel transaction = paymentServiceResult.getTransaction();
      return affirmPaymentTransactionStrategy.isTransactionSuccessfull(transaction);
   }

   @Override public boolean refundOrder(OrderModel order, BigDecimal amount) {
      AffirmPaymentTransactionEntryModel authorisationEntry = affirmPaymentTransactionStrategy.getAcceptedTransaction(order, PaymentTransactionType.AUTHORIZATION);
      AffirmPaymentTransactionEntryModel captureEntry = affirmPaymentTransactionStrategy.getAcceptedTransaction(order, PaymentTransactionType.CAPTURE);
      if(authorisationEntry == null || captureEntry == null){
         throw new AffirmPaymentException(AffirmPaymentConstants.REASON_CODE.INVALID_DATA, "Refund is not possible as authorisation and / or capture entry is not exist.");
      }
       if(amount != null) {
           amount = amount.multiply(BigDecimal.valueOf(100));
       }

       if(!affirmPaymentCoreService.isRefundPossible(order, amount)){
         throw new AffirmPaymentException(AffirmPaymentConstants.REASON_CODE.INVALID_DATA, "It is not possible to refund more that the order total.");
      }

      AffirmPaymentServiceRequest request = new AffirmRefundPaymentRequestServiceBuilder()
            .setOrder(order)
            .setChargeId(authorisationEntry.getProperties().get(ID))
            .setTransactionType(PaymentTransactionType.REFUND_STANDALONE)
            .setRefundAmount(amount)
            .build();

      AffirmPaymentServiceResult paymentServiceResult = affirmPaymentServiceExecutor.execute(request);

      PaymentTransactionEntryModel transaction = paymentServiceResult.getTransaction();
      return affirmPaymentTransactionStrategy.isTransactionSuccessfull(transaction);
   }

   @Override public boolean updateLoan(OrderModel order, AffirmLoanUpdateRequestData updateRequestData) {
      AffirmPaymentTransactionEntryModel authorisationEntry = affirmPaymentTransactionStrategy.getAcceptedTransaction(order, PaymentTransactionType.AUTHORIZATION);
      if(authorisationEntry == null){
         throw new AffirmPaymentException(AffirmPaymentConstants.REASON_CODE.INVALID_DATA, "Update is not possible as authorisation is not exist.");
      }

      AffirmPaymentServiceRequest request = new AffirmUpdatePaymentRequestServiceBuilder()
            .setOrder(order)
            .setChargeId(authorisationEntry.getProperties().get(ID))
            .setTransactionType(PaymentTransactionType.UPDATE)
            .setUpdateRequest(updateRequestData)
            .build();
      AffirmPaymentServiceResult paymentServiceResult = affirmPaymentServiceExecutor.execute(request);

      PaymentTransactionEntryModel transaction = paymentServiceResult.getTransaction();
      return affirmPaymentTransactionStrategy.isTransactionSuccessfull(transaction);
   }
}
