package com.affirm.payment.converter;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ClassificationData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import com.affirm.checkout.pojo.AffirmOrderItemTracking;
import com.affirm.checkout.pojo.AffirmOrderTracking;
import com.affirm.checkout.pojo.AffirmOrderTrackingResult;

public class AffirmOrderTrackingDataPopulator implements Populator<OrderData, AffirmOrderTrackingResult> {

   public static final String AFFIRM = "Affirm";
   @Resource private CommonI18NService commonI18NService;

   @Override public void populate(OrderData source, AffirmOrderTrackingResult target) throws ConversionException {
      Assert.notNull(source, "Parameter source cannot be null.");
      Assert.notNull(target, "Parameter target cannot be null.");

      String coupon = null;
      if(CollectionUtils.isNotEmpty(source.getAppliedVouchers())){
         coupon = source.getAppliedVouchers().stream() .collect(Collectors.joining(","));
      }
      //target.setOrderJson(new Gson().toJson(convertOrderDetails(source, coupon)));
      target.setOrder(convertOrderDetails(source, coupon));
      target.setProducts(convertOrderItems(source, coupon));
   }

   private AffirmOrderTracking convertOrderDetails(OrderData source, String coupon) {
      AffirmOrderTracking orderTracking = new AffirmOrderTracking();

      orderTracking.setCurrency(source.getTotalPrice().getCurrencyIso());
      if(source.getTotalDiscounts() != null) {
         orderTracking.setDiscount(source.getTotalDiscounts().getValue());
      }
      orderTracking.setCoupon(coupon);
      orderTracking.setOrderId(source.getCode());
      if(source.getPaymentInfo() != null) {
         orderTracking.setPaymentMethod(source.getPaymentInfo().getCardType());
      }
      orderTracking.setRevenue(multiply100(source.getSubTotal().getValue()));
      orderTracking.setShipping(multiply100(source.getDeliveryCost().getValue()));
      orderTracking.setShippingMethod(source.getDeliveryMode().getName());
      orderTracking.setStoreName(source.getStore());
      orderTracking.setTax(multiply100(source.getTotalTax().getValue()));
      orderTracking.setTotal(multiply100(source.getTotalPriceWithTax().getValue()));
      orderTracking.setCheckoutId(source.getGuid());
      if(source.getPaymentInfo() != null && source.getPaymentInfo().getCardType() != null ){
         orderTracking.setPaymentMethod(source.getPaymentInfo().getCardType());
      }else{
         orderTracking.setPaymentMethod(AFFIRM);
      }


      return orderTracking;
   }

   private BigDecimal multiply100(BigDecimal value) {
      if(value.doubleValue() > 0){
         return value.multiply(BigDecimal.valueOf(100));
      }
      return value;
   }

   private AffirmOrderItemTracking[] convertOrderItems(OrderData source, String coupon) {
      AffirmOrderItemTracking[] items = new AffirmOrderItemTracking[source.getEntries().size()];
      for (int i = 0; i < source.getEntries().size(); i++) {
         OrderEntryData entry =source.getEntries().get(i);
         AffirmOrderItemTracking item = new AffirmOrderItemTracking();
         item.setCoupon(coupon);
         item.setName(entry.getProduct().getName());
         item.setPrice(multiply100(entry.getBasePrice().getValue()));
         item.setProductId(entry.getProduct().getCode());
         item.setQuantity(entry.getQuantity());
         item.setBrand(entry.getProduct().getManufacturer());
         item.setCurrency(source.getTotalPrice().getCurrencyIso());
         item.setVariant(getColour(entry.getProduct()));
         if(CollectionUtils.isNotEmpty(entry.getProduct().getCategories())){
            item.setCategory(entry.getProduct().getCategories().iterator().next().getName());
         }
         items[i] = item;
      }
      return items;
   }

   private String getColour(final ProductData product)
   {
      if(product.getClassifications() != null){
         Optional<ClassificationData> colourOp = product.getClassifications().stream().filter(c -> "Colour".equals(c.getName())).findAny();
         if(colourOp.isPresent()){
            ClassificationData classificationData = colourOp.get();
            if(CollectionUtils.isNotEmpty(classificationData.getFeatures())){
               FeatureData featureData = classificationData.getFeatures().iterator().next();
               return Optional.ofNullable(featureData.getFeatureValues()).orElse(Collections.emptyList()).stream().map(f -> f.getValue()).collect(Collectors.joining(","));
            }
         }
      }
      return null;
   }

}
