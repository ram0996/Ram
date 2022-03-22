package com.affirm.payment.service.executor.request.populator;

import com.affirm.payment.AffirmPaymentConstants;
import com.affirm.payment.service.executor.request.AffirmPaymentServiceRequest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.HashMap;

public class AffirmRefundRequestPopulator  implements Populator<AffirmPaymentServiceRequest,HashMap> {
   @Override public void populate(AffirmPaymentServiceRequest request, HashMap map) throws ConversionException {
      Object value = request.getParams().get(AffirmPaymentConstants.REFUND.AMOUNT);
      if(value != null) {
         map.put(AffirmPaymentConstants.REFUND.AMOUNT, value);
      }
   }
}