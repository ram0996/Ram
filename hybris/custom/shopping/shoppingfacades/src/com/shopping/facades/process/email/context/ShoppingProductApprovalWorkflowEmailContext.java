/**
 *
 */
package com.shopping.facades.process.email.context;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;

import org.apache.velocity.VelocityContext;


/**
 * @author venkat
 *
 */
public class ShoppingProductApprovalWorkflowEmailContext extends VelocityContext
{
	public void init(final ProductModel product, final UserModel user)
	{
		put("product", product);
		put("user", user);
	}
}
