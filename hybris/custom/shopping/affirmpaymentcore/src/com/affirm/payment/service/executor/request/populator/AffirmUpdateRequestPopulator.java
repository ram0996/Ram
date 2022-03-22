package com.affirm.payment.service.executor.request.populator;

import com.affirm.checkout.pojo.AffirmLoanUpdateRequestData;
import com.affirm.payment.AffirmPaymentConstants;
import com.affirm.payment.service.executor.request.AffirmPaymentServiceRequest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.HashMap;

public class AffirmUpdateRequestPopulator implements Populator<AffirmPaymentServiceRequest,HashMap> {
   @Override public void populate(AffirmPaymentServiceRequest request, HashMap map) throws ConversionException {
      AffirmLoanUpdateRequestData requestData = (AffirmLoanUpdateRequestData) request.getParams().get(AffirmPaymentConstants.UPDATE.REQUEST);
      if(requestData == null){
         return;
      }

      if(requestData.getOrderId() != null) {
         map.put(AffirmPaymentConstants.UPDATE.ORDER_ID, requestData.getOrderId());
      }
      if(requestData.getShipping() != null) {
         map.put(AffirmPaymentConstants.UPDATE.SHIPPING, requestData.getShipping());
      }
      if(requestData.getShippingCarrier() != null) {
         map.put(AffirmPaymentConstants.UPDATE.SHIPPING_CARRIER, requestData.getShippingCarrier());
      }
      if(requestData.getShippingConfirmation() != null) {
         map.put(AffirmPaymentConstants.UPDATE.SHIPPING_CONFIRMATION, requestData.getShippingConfirmation());
      }
   }
}