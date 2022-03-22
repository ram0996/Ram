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
package com.affirm.payment.core.service.impl;

import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.AffirmPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.affirm.payment.core.service.AffirmPaymentCoreService;
import com.affirm.payment.core.service.AffirmPaymentTransactionStrategy;
import com.affirm.payment.dao.AffirmOrderDAO;
import com.affirm.payment.model.AffirmConfigContainerModel;
import com.affirm.payment.model.PaymentThresholdModel;

public class DefaultAffirmPaymentCoreService implements AffirmPaymentCoreService
{

   private static final Logger LOG = LoggerFactory.getLogger(DefaultAffirmPaymentCoreService.class);


   @Resource private MediaService mediaService;
   @Resource private ModelService modelService;
   @Resource private FlexibleSearchService flexibleSearchService;
   @Resource private AffirmOrderDAO affirmOrderDAO;
   @Resource private AffirmPaymentTransactionStrategy affirmPaymentTransactionStrategy;


	@Override
	public String getHybrisLogoUrl(final String logoCode)
	{
		final MediaModel media = mediaService.getMedia(logoCode);

		// Keep in mind that with Slf4j you don't need to check if debug is enabled, it is done under the hood.
		LOG.debug("Found media [code: {}]", media.getCode());

		return media.getURL();
	}

	@Override
	public void createLogo(final String logoCode)
	{
		final Optional<CatalogUnawareMediaModel> existingLogo = findExistingLogo(logoCode);

		final CatalogUnawareMediaModel media = existingLogo.isPresent() ? existingLogo.get()
				: modelService.create(CatalogUnawareMediaModel.class);
		media.setCode(logoCode);
		media.setRealFileName("sap-hybris-platform.png");
		modelService.save(media);

		mediaService.setStreamForMedia(media, getImageStream());
	}

	private final static String FIND_LOGO_QUERY = "SELECT {" + CatalogUnawareMediaModel.PK + "} FROM {"
			+ CatalogUnawareMediaModel._TYPECODE + "} WHERE {" + CatalogUnawareMediaModel.CODE + "}=?code";

	private Optional<CatalogUnawareMediaModel> findExistingLogo(final String logoCode)
	{
		final FlexibleSearchQuery fQuery = new FlexibleSearchQuery(FIND_LOGO_QUERY);
		fQuery.addQueryParameter("code", logoCode);

		try
		{
			return Optional.of(flexibleSearchService.searchUnique(fQuery));
		}
		catch (final SystemException e)
		{
			return Optional.empty();
		}
	}

   @Override public AffirmConfigContainerModel getPaymentConfiguration(CartModel cart) {
      return cart.getSite().getAffirmConfigContainer();
   }

   @Override public PaymentModeModel getPaymentMode() {
      PaymentModeModel paymentModeModel = new PaymentModeModel();
      paymentModeModel.setCode(PAYMENT_MODE_AFFIRM.toLowerCase());
      return flexibleSearchService.getModelByExample(paymentModeModel);
   }

   @Override public AbstractOrderModel getOrderByCode(String orderId) {
      return affirmOrderDAO.getOrderByCode(orderId);
   }

   @Override public boolean isRefundPossible(OrderModel order, BigDecimal amount) {
      BigDecimal refundedAmount = affirmPaymentTransactionStrategy.getRefundedAmount(order);
      BigDecimal carttotal = BigDecimal.valueOf(order.getTotalIncludingTax()).multiply(BigDecimal.valueOf(100));
      if(amount == null){
         return  refundedAmount.compareTo(carttotal) < 0;
      }

      return refundedAmount.add(amount).compareTo(carttotal) <= 0;
   }

   @Override public boolean isAffirmOrder(OrderModel order) {
	   if(order.getPaymentInfo() instanceof AffirmPaymentInfoModel){
	      return true;
      }
      if(order.getPaymentInfo() instanceof CreditCardPaymentInfoModel){
	      return StringUtils.isNotEmpty(((CreditCardPaymentInfoModel) order.getPaymentInfo()).getAffirmVCNId());
      }
      return  false;
   }

   @Override public boolean isVCNOrder(OrderModel order) {
         if(order.getPaymentInfo() instanceof CreditCardPaymentInfoModel){
            return StringUtils.isNotEmpty(((CreditCardPaymentInfoModel) order.getPaymentInfo()).getAffirmVCNId());
         }
         return false;
   }

   @Override public boolean isPaymentModeSupported(CartModel cartModel, PaymentModeModel paymentModeModel) {
      if(CollectionUtils.isEmpty(paymentModeModel.getThresholds())){
         return true;
      }
      final Double totalPrice = cartModel.getTotalIncludingTax();

      boolean isAllowed = true;
      for (final PaymentThresholdModel threshold : paymentModeModel.getThresholds())
      {
         if (!threshold.getCurrency().equals(cartModel.getCurrency())) {
            continue;
         }

         if (threshold.isMinimumMode() && totalPrice.doubleValue() < threshold.getThreshold().doubleValue())
         {
            isAllowed = false;
         }
         if(!threshold.isMinimumMode() && totalPrice.doubleValue() > threshold.getThreshold().doubleValue()){
            isAllowed = false;
         }
      }

      return isAllowed;
   }

   private InputStream getImageStream()
	{
		return DefaultAffirmPaymentCoreService.class.getResourceAsStream("/affirmpaymentcore/sap-hybris-platform.png");
	}

}
