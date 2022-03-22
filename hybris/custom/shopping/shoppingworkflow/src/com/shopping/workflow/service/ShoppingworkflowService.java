/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.shopping.workflow.service;

import de.hybris.platform.core.model.ItemModel;

import java.util.List;


public interface ShoppingworkflowService
{
	String getHybrisLogoUrl(String logoCode);

	void createLogo(String logoCode);

	List<ItemModel> getAllProductsForCheckStatus();

	List<ItemModel> getItemsListForQuery(final String stringQuery);
}
