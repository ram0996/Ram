package com.affirm.payment.service;

import com.affirm.checkout.pojo.AffirmLoanUpdateRequestData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;

import java.math.BigDecimal;

public interface AffirmPaymentService {

   void initizePayment(CartModel cart, AddressModel addressModel);

   boolean authorisePayment(CartModel sessionCart, String checkoutToken);

   boolean capturePayment(AbstractOrderModel order);

   boolean voidPayment(AbstractOrderModel order);

   boolean refundOrder(OrderModel order, BigDecimal amount);

   boolean updateLoan(OrderModel order, AffirmLoanUpdateRequestData updateRequestData);
}
