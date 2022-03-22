/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.shopping.workflow.process.action;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.processengine.action.AbstractAction;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.task.RetryLaterException;

import java.util.Set;

import javax.annotation.Resource;

import com.shopping.workflow.model.NewPartProcessModel;


/**
 *
 */
public class RemindMeLaterNotificationAction extends AbstractAction<NewPartProcessModel>
{

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "cronJobService")
	private CronJobService cronJobService;

	@Resource(name = "i18nService")
	private I18NService i18nService;

	@Resource(name = "userService")
	private UserService userService;

	@Override
	public String execute(final NewPartProcessModel bpm) throws RetryLaterException, Exception
	{
		final ProductModel product = bpm.getPart();
		cronJobService.getCronJob(product.getCode() + "_REMINDMELATER");
		return "OK";
	}

	@Override
	public Set<String> getTransitions()
	{
		final Set<String> transitions = super.createTransitions("OK");
		transitions.add("NOK");
		return transitions;
	}

}
