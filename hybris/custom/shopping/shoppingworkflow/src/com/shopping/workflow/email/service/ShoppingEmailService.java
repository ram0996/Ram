/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.shopping.workflow.email.service;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.List;


/**
 *
 */
public interface ShoppingEmailService
{
	void sendEmailForNewPartApproval(ProductModel part, UserModel user);

	void sendEmailToApprover(final List<ItemModel> getAllProducts);
}
