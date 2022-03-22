/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.shopping.workflow.email.context;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;

import org.apache.velocity.VelocityContext;

/**
 *
 */
public class ShoppingWorkflowEmailContext extends VelocityContext
{
	public void init(final ProductModel product, final UserModel user)
	{
		put("fromEmail", "donotreply@shopping.com");
		put("product", product);
		put("user", user);
	}

	public String getFromEmail()
	{
		return get("fromEmail").toString();
	}
}
