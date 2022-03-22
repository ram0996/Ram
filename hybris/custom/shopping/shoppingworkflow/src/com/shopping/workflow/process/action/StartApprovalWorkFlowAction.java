/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.shopping.workflow.process.action;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.task.RetryLaterException;

import java.util.Objects;
import java.util.Set;

import com.shopping.workflow.model.NewPartProcessModel;


/**
 *
 */
public class StartApprovalWorkFlowAction extends AbstractAction<NewPartProcessModel>
{

	@Override
	public String execute(final NewPartProcessModel bpm) throws RetryLaterException, Exception
	{
		final ProductModel product = bpm.getPart();
		if(Objects.nonNull(product))
		{
			if(ArticleApprovalStatus.APPROVED.equals(product.getApprovalStatus()) || ArticleApprovalStatus.UNAPPROVED.equals(product.getApprovalStatus()))
			{
				return "OK";
			}else
			{
				return "WAIT";
			}
		}
		return "NO_PM_FOUND";
	}

	@Override
	public Set<String> getTransitions()
	{
		final Set<String> transitions = super.createTransitions("WAIT");
		transitions.add("OK");
		transitions.add("NO_PM_FOUND");
		return transitions;

	}

}
