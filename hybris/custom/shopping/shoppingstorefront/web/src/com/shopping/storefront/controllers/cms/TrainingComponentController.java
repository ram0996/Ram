/**
 *
 */
package com.shopping.storefront.controllers.cms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shopping.core.model.TrainingComponentModel;
import com.shopping.storefront.controllers.ControllerConstants;


/**
 * @author venka
 *
 */
@Controller("TrainingComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.TrainingCmsComponent)
public class TrainingComponentController extends AbstractAcceleratorCMSComponentController<TrainingComponentModel>
{

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final TrainingComponentModel component)
	{
		model.addAttribute("component1", component);
		final String url = request.getRequestURL().toString();
		if (StringUtils.isNotBlank(url))
		{
			if (url.contains("landingLayout2Page.jsp"))
			{
				model.addAttribute("currentPage", "Home Page");
			}
			else if (url.contains("/category/"))
			{
				model.addAttribute("currentPage", "Category Page");
			}
			else if (url.contains("/product/"))
			{
				model.addAttribute("currentPage", "Product Page");
			}
			else if (url.contains("/cart/"))
			{
				model.addAttribute("currentPage", "Cart Page");
			}
			else if (url.contains("/checkout/"))
			{
				model.addAttribute("currentPage", "Checkout Page");
			}
			else if (url.contains("/checkoutConfirmationLayoutPage"))
			{
				model.addAttribute("currentPage", "Confirmation Page");
			}
			else if (url.contains("/account/"))
			{
				model.addAttribute("currentPage", "My Account Page");
			}
			else if (url.contains("/search/"))
			{
				model.addAttribute("currentPage", "Search Page");
			}
		}
	}

}
