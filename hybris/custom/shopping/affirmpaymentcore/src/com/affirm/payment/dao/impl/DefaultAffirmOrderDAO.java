package com.affirm.payment.dao.impl;

import com.affirm.payment.dao.AffirmOrderDAO;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import javax.annotation.Resource;

public class DefaultAffirmOrderDAO implements AffirmOrderDAO {

   @Resource FlexibleSearchService flexibleSearchService;

   @Override public AbstractOrderModel getOrderByCode(String orderId) {
      AbstractOrderModel sampleOrder = new AbstractOrderModel();
      sampleOrder.setCode(orderId);
      return flexibleSearchService.getModelByExample(sampleOrder);
   }
}
