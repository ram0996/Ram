/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.shopping.workflow.job;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.task.TaskConditionModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shopping.workflow.model.NewPartProcessModel;
import com.shopping.workflow.model.NewPartsCronJobModel;
import com.shopping.workflow.service.ShoppingworkflowService;

/**
 *
 */
public class NewPartsJob extends AbstractJobPerformable<NewPartsCronJobModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(NewPartsJob.class);

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Resource(name = "businessProcessService")
	private BusinessProcessService businessProcessService;

	@Resource(name = "shoppingworkflowService")
	private ShoppingworkflowService shoppingworkflowService;

	@Override
	public PerformResult perform(final NewPartsCronJobModel cronJob)
	{
		if(StringUtils.isBlank(cronJob.getFlexiSearch()))
		{
			return new PerformResult(CronJobResult.ERROR, CronJobStatus.ABORTED);
		}

		final List<ItemModel> itemList = shoppingworkflowService.getItemsListForQuery(cronJob.getFlexiSearch());

		if (itemList == null)
		{
			LOG.error("No new parts found");
			return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.ABORTED);
		}

		final UserGroupModel partnerManagerGroup = userService.getUserGroupForUID("globalproductauthorgroup");

		for (final ItemModel item : itemList)
		{
			final ProductModel part = (ProductModel) item;

			UserModel workflowUser = null;

			try
			{
				workflowUser = userService.getUserForUID("venkat05.hybris@gmail.com");
			}
			catch (final UnknownIdentifierException e)
			{
				LOG.warn("user was not found");
			}

			if (Objects.nonNull(workflowUser))
			{
				if (businessProcessService.getProcess(part.getCode()) == null)
				{
					cleanupOldTasksAndWorkflows(part);
					final NewPartProcessModel newPartProcess = (NewPartProcessModel) businessProcessService.createProcess(
							part.getCode(), "newPartDefinition");
					newPartProcess.setPart(part);
					// we have to delay the process because the email service
					// would
					// create only separate emails for new part instead of
					// regrouping
					try
					{
						Thread.sleep(500); // 1000 milliseconds is one second.
					}
					catch (final InterruptedException ex)
					{
						Thread.currentThread().interrupt();
					}
					businessProcessService.startProcess(newPartProcess);
				}
			}
			else
			{
				LOG.warn("No business process created for Part:" + part.getCode() + ": - No users found ");
			}
		}
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);

	}

	/**
	 *
	 */
	private void cleanupOldTasksAndWorkflows(final ProductModel product)
	{
		final SearchResult<TaskConditionModel> tcResult = flexibleSearchService
				.search("SELECT {" + TaskConditionModel.PK + "} FROM {" + TaskConditionModel._TYPECODE + "} WHERE {"
						+ TaskConditionModel.UNIQUEID + "} like '" + product.getCode() + "%'");
		if (Objects.nonNull(tcResult) && CollectionUtils.isNotEmpty(tcResult.getResult()))
		{
			modelService.removeAll(tcResult.getResult());
		}

		final SearchResult<TaskConditionModel> workflowResult = flexibleSearchService
				.search("SELECT {" + WorkflowModel.PK + "} FROM {" + WorkflowModel._TYPECODE + "} WHERE {" + WorkflowModel.NAME
						+ "} like '%" + product.getCode() + "'");
		if (Objects.nonNull(workflowResult) && CollectionUtils.isNotEmpty(workflowResult.getResult()))
		{
			modelService.removeAll(workflowResult.getResult());
		}
	}
}
