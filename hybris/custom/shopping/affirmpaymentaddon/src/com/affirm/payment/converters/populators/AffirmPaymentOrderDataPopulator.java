package com.affirm.payment.converters.populators;

import com.affirm.payment.core.service.AffirmPaymentCoreService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.AffirmPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import javax.annotation.Resource;

public class AffirmPaymentOrderDataPopulator implements Populator<OrderModel, OrderData> {

   @Resource AffirmPaymentCoreService affirmPaymentCoreService;
   @Resource AbstractPopulatingConverter<AffirmPaymentInfoModel, CCPaymentInfoData> affirmPaymentInfoConverter;


   @Override public void populate(OrderModel orderModel, OrderData orderData) throws ConversionException {
      if(affirmPaymentCoreService.isAffirmOrder(orderModel)) {
         orderData.setPaymentMode(AffirmPaymentCoreService.PAYMENT_MODE_AFFIRM);
      }

      if(orderData.getPaymentInfo() == null){
         populatePaymentInfo(orderModel, orderData);
      }
   }

   private void populatePaymentInfo(OrderModel source, OrderData orderData) {
      //TODO This could be AbstractOrderPopulator.java class but Credit card payment info type is hardcoded.
      final PaymentInfoModel paymentInfo = source.getPaymentInfo();
      if (paymentInfo instanceof AffirmPaymentInfoModel)
      {
         final CCPaymentInfoData paymentInfoData = affirmPaymentInfoConverter.convert((AffirmPaymentInfoModel) paymentInfo);
         orderData.setPaymentInfo(paymentInfoData);
      }
   }
}
