package com.affirm.payment.service.executor.request.populator;

import com.affirm.payment.AffirmPaymentConstants;
import com.affirm.payment.service.executor.request.AffirmPaymentServiceRequest;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.HashMap;

public class AffirmAuthRequestPopulator implements Populator<AffirmPaymentServiceRequest,HashMap> {
   @Override public void populate(AffirmPaymentServiceRequest request, HashMap map) throws ConversionException {
      AbstractOrderModel cart = (AbstractOrderModel) request.getParams().get(AffirmPaymentConstants.ORDER);
      String checkoutToken = (String) request.getParams().get(AffirmPaymentConstants.Authorisation.CHECKOUT_TOKEN);
      map.put(AffirmPaymentConstants.Authorisation.CHECKOUT_TOKEN, checkoutToken);
      map.put(AffirmPaymentConstants.ORDER_ID, cart.getCode());

   }
}
