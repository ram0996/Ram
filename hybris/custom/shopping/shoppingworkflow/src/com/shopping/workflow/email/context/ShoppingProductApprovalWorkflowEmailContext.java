package com.shopping.workflow.email.context;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.VelocityContext;


public class ShoppingProductApprovalWorkflowEmailContext extends VelocityContext
{
	public void init(final List<ItemModel> getAllProducts, final UserModel user)
	{
		final List<ProductModel> products = new ArrayList<>();
		for (final ItemModel itemModel : getAllProducts)
		{
			final ProductModel product = (ProductModel) itemModel;
			products.add(product);
		}
		put("product", products);
		put("user", user);
	}
}
