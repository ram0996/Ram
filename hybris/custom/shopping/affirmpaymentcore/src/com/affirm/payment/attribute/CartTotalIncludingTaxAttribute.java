package com.affirm.payment.attribute;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CartTotalIncludingTaxAttribute  extends AbstractDynamicAttributeHandler<Double, AbstractOrderModel> {


   @Override public Double get(AbstractOrderModel abstractOrderModel) {
      if (abstractOrderModel == null) {
         throw new IllegalArgumentException("address model is required");
      } else {
         if(abstractOrderModel.getNet() == Boolean.TRUE){
            BigDecimal total = BigDecimal.valueOf(abstractOrderModel.getTotalPrice());
            if(abstractOrderModel.getNet() == Boolean.TRUE && abstractOrderModel.getTotalTax() != null) {
               total = total.add(BigDecimal.valueOf(abstractOrderModel.getTotalTax())).setScale(2, RoundingMode.HALF_UP);
            }
            return total.doubleValue();
         }else{
            return abstractOrderModel.getTotalPrice();
         }
      }
   }

   @Override public void set(AbstractOrderModel addressModel, Double value) {
      //do nothing
   }
}