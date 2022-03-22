package com.shopping.workflow.interceptor;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.shopping.workflow.model.NewPartProcessModel;


/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

/**
 *
 */
public class ShoppingProductPrepareInterceptor implements PrepareInterceptor<ProductModel>
{

	@Autowired
	private BusinessProcessService businessProcessService;

	@Override
	public void onPrepare(final ProductModel productModel, final InterceptorContext ctx) throws InterceptorException
	{
		final Set<String> dirtyAttributes = productModel.getItemModelContext().getDirtyAttributes();
		if (null != dirtyAttributes) {
			final NewPartProcessModel bpm = (NewPartProcessModel) businessProcessService.getProcess(productModel.getCode());
			for (final String attributeModify : dirtyAttributes)
			{
				if (Objects.nonNull(bpm) && StringUtils.isNotBlank(attributeModify)
						&& attributeModify.equalsIgnoreCase("approvalStatus")
						&& !productModel.getApprovalStatus().equals(ArticleApprovalStatus.CHECK))
				{
					businessProcessService.triggerEvent("PROMOTE_WORKFLOW_COMPLETE_EVENT");
				}
			}
		}

	}
}
