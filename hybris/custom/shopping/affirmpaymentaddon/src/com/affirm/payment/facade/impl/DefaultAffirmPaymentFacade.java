package com.affirm.payment.facade.impl;

import com.affirm.checkout.pojo.AffirmLoanUpdateRequestData;
import com.affirm.payment.AffirmPaymentConstants;
import com.affirm.payment.core.service.AffirmPaymentCoreService;
import com.affirm.payment.exception.AffirmPaymentException;
import com.affirm.payment.facade.AffirmPaymentFacade;
import com.affirm.payment.service.AffirmPaymentService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.math.BigDecimal;

public class DefaultAffirmPaymentFacade implements AffirmPaymentFacade {

   private static final Logger LOG = LoggerFactory.getLogger(DefaultAffirmPaymentFacade.class);

   @Resource private CartService cartService;
   @Resource private CMSSiteService cmsSiteService;
   @Resource private Converter<AddressData, AddressModel> addressReverseConverter;
   @Resource private AffirmPaymentService affirmPaymentService;
   @Resource private AffirmPaymentCoreService affirmPaymentCoreService;

   @Override public void initizePayment(AddressData addressData) {
      if(!cartService.hasSessionCart()){
         throw new AffirmPaymentException(AffirmPaymentConstants.REASON_CODE.INVALID_DATA, "cart is not exist");
      }

      AddressModel addressModel = addressReverseConverter.convert(addressData);
      addressModel.setStreetname(addressData.getLine1());
      CartModel cart = cartService.getSessionCart();

      affirmPaymentService.initizePayment(cart, addressModel);
   }

   @Override public boolean authorisePayment(String checkoutToken) {
      if(checkoutToken == null){
         throw new AffirmPaymentException(AffirmPaymentConstants.REASON_CODE.INVALID_DATA, "checkoutToken cannot be empty");
      }
      if(!cartService.hasSessionCart()){
         throw new AffirmPaymentException(AffirmPaymentConstants.REASON_CODE.INVALID_DATA, "Cart cannot be empty");
      }

      return affirmPaymentService.authorisePayment(cartService.getSessionCart(), checkoutToken);

   }

   @Override public boolean capturePayment(String orderId) {
      AbstractOrderModel order = getAbstractOrderModel(orderId);
      return affirmPaymentService.capturePayment(order);
   }


   @Override public boolean voidPayment(String orderId) {
      AbstractOrderModel order = getAbstractOrderModel(orderId);
      return  affirmPaymentService.voidPayment(order);
   }

   @Override public boolean isAffirmPaymentEnabled() {
      CMSSiteModel siteModel = cmsSiteService.getCurrentSite();
      return siteModel.getAffirmConfigContainer() != null && siteModel.getAffirmConfigContainer().isEnabled() && StringUtils.isNotEmpty(siteModel.getAffirmConfigContainer().getAffirmPublicKey());
   }

   @Override public boolean refundOrder(String orderId, BigDecimal amount) {
      OrderModel order = (OrderModel) affirmPaymentCoreService.getOrderByCode(orderId);
      return affirmPaymentService.refundOrder(order, amount);
   }

   @Override public boolean updateLoan(String orderId, AffirmLoanUpdateRequestData updateRequestData) {
      OrderModel order = (OrderModel) affirmPaymentCoreService.getOrderByCode(orderId);
      return affirmPaymentService.updateLoan(order, updateRequestData);
   }

   private AbstractOrderModel getAbstractOrderModel(String orderId) {
      try {
         return affirmPaymentCoreService.getOrderByCode(orderId);
      }catch (ModelNotFoundException ne){
         throw new AffirmPaymentException(AffirmPaymentConstants.REASON_CODE.INVALID_DATA, "order is not found with the given id");
      }
   }

   @Override public boolean isAffirmPaymentEnabledForCurrentCart() {
      return affirmPaymentCoreService.isPaymentModeSupported(cartService.getSessionCart(), affirmPaymentCoreService.getPaymentMode());
   }

   @Override public boolean isAffirmOrder(OrderData orderData) {
      return AffirmPaymentCoreService.PAYMENT_MODE_AFFIRM.equals(orderData.getPaymentMode());
   }
}
