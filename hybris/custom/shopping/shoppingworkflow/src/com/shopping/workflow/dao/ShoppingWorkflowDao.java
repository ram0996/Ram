/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.shopping.workflow.dao;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.List;


/**
 *
 */
public interface ShoppingWorkflowDao
{
	List<ItemModel> getAllProductsForCheckStatus();

	List<ItemModel> getItemsListForQuery(final String stringQuery);

	WorkflowTemplateModel getWorkflowTemplateForCode(final String code);
}
