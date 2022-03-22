/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.affirm.payment.core.service;

import com.affirm.payment.model.AffirmConfigContainerModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;

import java.math.BigDecimal;

public interface AffirmPaymentCoreService
{
   String PAYMENT_MODE_AFFIRM = "Affirm";

   String getHybrisLogoUrl(String logoCode);

	void createLogo(String logoCode);

   PaymentModeModel getPaymentMode();

   AffirmConfigContainerModel getPaymentConfiguration(CartModel cart);


   AbstractOrderModel getOrderByCode(String orderId);

   boolean isRefundPossible(OrderModel order, BigDecimal amount);

   boolean isPaymentModeSupported(CartModel cart, PaymentModeModel paymentModeModel);

   boolean isAffirmOrder(OrderModel order);

   boolean isVCNOrder(OrderModel order);
}
