package com.affirm.payment.dao;

import de.hybris.platform.core.model.order.AbstractOrderModel;

/**
 * Interface to implement order related database queries
 */
public interface AffirmOrderDAO {
   AbstractOrderModel getOrderByCode(String orderId);
}
