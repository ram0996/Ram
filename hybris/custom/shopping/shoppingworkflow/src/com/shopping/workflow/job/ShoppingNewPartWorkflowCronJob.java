/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.shopping.workflow.job;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.WorkflowService;
import de.hybris.platform.workflow.model.WorkflowModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.shopping.workflow.dao.ShoppingWorkflowDao;
import com.shopping.workflow.email.service.ShoppingEmailService;


/**
 *
 */
public class ShoppingNewPartWorkflowCronJob extends AbstractJobPerformable<CronJobModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(ShoppingNewPartWorkflowCronJob.class);

	private static final String WORKFLOW_TEMPLATE = "PartApprovalWorkflowTemplate";
	private static final String APPROVER_UID = "globalproductapprover";
	private static final String WORKFLOW_COMMENTS = "Please approve changes to Heading";

	@Resource(name = "modelService")
	private ModelService modelService;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "newestWorkflowService")
	private WorkflowService workflowService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "cronJobService")
	private CronJobService cronJobService;

	@Resource(name = "shoppingWorkflowDao")
	private ShoppingWorkflowDao shoppingWorkflowDao;

	@Resource(name = "shoppingEmailService")
	private ShoppingEmailService shoppingEmailService;

	@Override
	public PerformResult perform(final CronJobModel arg0)
	{
		final WorkflowTemplateModel workflowTemplateModel = shoppingWorkflowDao.getWorkflowTemplateForCode(WORKFLOW_TEMPLATE);
		final UserModel workflowOwner = userService.getUserForUID(APPROVER_UID);

		LOG.info("UserModel is {} and Workflow template name is {}", workflowOwner.getUid(),
				workflowTemplateModel.getName(Locale.ENGLISH));
		final List<ItemModel> getAllProducts = shoppingWorkflowDao.getAllProductsForCheckStatus();
		WorkflowModel workflowModel = null;
		if (CollectionUtils.isNotEmpty(getAllProducts))
		{
			workflowModel = workflowService.createWorkflow(workflowTemplateModel.getName(), workflowTemplateModel, getAllProducts,
					workflowOwner);
		}
		if (Objects.nonNull(workflowModel))
		{
			LOG.info("Saved the workflow model with code " + workflowModel.getCode());
			cronJobService.performCronJob(workflowModel);

			try
			{
				shoppingEmailService.sendEmailToApprover(getAllProducts);
			}
			catch (final Exception e)
			{
				LOG.error("While sending an email ", e);
			}
		}
		return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
	}

}
