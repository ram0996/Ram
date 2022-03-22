/**
 *
 */
package com.shopping.storefront.controllers.cms;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.cms.AbstractCMSComponentController;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.shopping.core.model.ShoppingVideoComponentModel;
import com.shopping.storefront.controllers.ControllerConstants;


/**
 * @author venka
 *
 */
@Controller("ShoppingVideoCMSComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.ShoppingVideoCmsComponent)
public class ShoppingVideoCMSComponentController extends AbstractCMSComponentController<ShoppingVideoComponentModel>
{

	@Override
	protected void fillModel(final HttpServletRequest request, final Model model, final ShoppingVideoComponentModel component)
	{
		model.addAttribute("autoPlay", component.getAutoPlay() ? 1:0);
		model.addAttribute("showControls", component.getShowControls() ? 1:0);
		model.addAttribute("height", component.getHeight());
		model.addAttribute("width", component.getWidth());
		model.addAttribute("videoId", component.getVideoId());
	}

	@Override
	protected String getView(final ShoppingVideoComponentModel component)
	{
		return null;
	}

}
