package com.affirm.payment.facade.impl;

import de.hybris.platform.acceleratorfacades.flow.impl.SessionOverrideCheckoutFlowFacade;
import de.hybris.platform.acceleratorfacades.order.impl.DefaultAcceleratorCheckoutFacade;

public class ExtendedDefaultAcceleratorCheckoutFacade extends SessionOverrideCheckoutFlowFacade{

   @Override public boolean hasNoPaymentInfo() {
      return getCartService().getSessionCart().getPaymentInfo() == null;
   }

}
