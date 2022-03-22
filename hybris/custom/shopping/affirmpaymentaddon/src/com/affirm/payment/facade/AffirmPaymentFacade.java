package com.affirm.payment.facade;

import com.affirm.checkout.pojo.AffirmLoanUpdateRequestData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;

import java.math.BigDecimal;

public interface AffirmPaymentFacade {

   boolean isAffirmPaymentEnabled();

   boolean authorisePayment(String checkoutToken);

   boolean capturePayment(String orderId);

   boolean voidPayment(String orderId);

   boolean refundOrder(String orderId, BigDecimal amount);

   void initizePayment(AddressData convert);

   boolean updateLoan(String orderId, AffirmLoanUpdateRequestData updateRequestData);

   boolean isAffirmPaymentEnabledForCurrentCart();

   boolean isAffirmOrder(OrderData orderData);
}
