/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.shopping.workflow.process.action;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.task.RetryLaterException;

import java.util.Objects;
import java.util.Set;

import javax.annotation.Resource;

import com.shopping.workflow.email.service.ShoppingEmailService;
import com.shopping.workflow.model.NewPartProcessModel;


/**
 *
 */
public class InformNewPartAction extends AbstractAction<NewPartProcessModel>
{

	@Resource(name = "shoppingEmailService")
	private ShoppingEmailService shoppingEmailService;

	@Resource(name = "userService")
	private UserService userService;

	@Override
	public String execute(final NewPartProcessModel bpm) throws RetryLaterException, Exception
	{
		final ProductModel part = bpm.getPart();
		if (Objects.nonNull(part.getApprovalStatus()) && ArticleApprovalStatus.APPROVED.equals(part.getApprovalStatus()))
		{
			return "PART_APPROVED";
		}
		else
		{
			final UserModel user = userService.getUserForUID("venkat05.hybris@gmail.com");
			shoppingEmailService.sendEmailForNewPartApproval(part, user);

			return "OK";
		}
	}

	@Override
	public Set<String> getTransitions()
	{
		final Set<String> transitions = super.createTransitions("OK");
		transitions.add("PART_APPROVED");
		return transitions;
	}

}
