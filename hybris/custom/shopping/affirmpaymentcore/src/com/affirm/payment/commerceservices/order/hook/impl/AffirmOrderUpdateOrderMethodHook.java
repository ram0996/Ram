package com.affirm.payment.commerceservices.order.hook.impl;

import com.affirm.checkout.pojo.AffirmLoanUpdateRequestData;
import com.affirm.payment.core.service.AffirmPaymentCoreService;
import com.affirm.payment.service.AffirmPaymentService;
import de.hybris.platform.commerceservices.order.hook.CommercePlaceOrderMethodHook;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.InvalidCartException;
import org.apache.log4j.Logger;

public class AffirmOrderUpdateOrderMethodHook implements CommercePlaceOrderMethodHook {

   private static final Logger LOG = Logger.getLogger(AffirmOrderUpdateOrderMethodHook.class);

   private AffirmPaymentCoreService affirmPaymentCoreService;
   private AffirmPaymentService affirmPaymentService;

   @Override public void afterPlaceOrder(CommerceCheckoutParameter parameter, CommerceOrderResult orderModel) throws InvalidCartException {
      OrderModel order = orderModel.getOrder();
      if(affirmPaymentCoreService.isAffirmOrder(order) && !affirmPaymentCoreService.isVCNOrder(order)){
         AffirmLoanUpdateRequestData updateRequestData = new AffirmLoanUpdateRequestData();
         updateRequestData.setOrderId(order.getCode());
         try {
            affirmPaymentService.updateLoan(order, updateRequestData);
         }catch (RuntimeException re){
            LOG.error("Unexpected issue during updating the loan", re);
         }
      }
   }

   @Override public void beforePlaceOrder(CommerceCheckoutParameter parameter) throws InvalidCartException {

   }

   @Override public void beforeSubmitOrder(CommerceCheckoutParameter parameter, CommerceOrderResult result) throws InvalidCartException {

   }

   public void setAffirmPaymentCoreService(AffirmPaymentCoreService affirmPaymentCoreService) {
      this.affirmPaymentCoreService = affirmPaymentCoreService;
   }

   public void setAffirmPaymentService(AffirmPaymentService affirmPaymentService) {
      this.affirmPaymentService = affirmPaymentService;
   }
}