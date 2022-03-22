package com.affirm.payment.category.cmsrenderer;

import de.hybris.platform.addonsupport.renderer.impl.DefaultAddOnCMSComponentRenderer;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;

import javax.servlet.ServletException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Map;

public class AffirmProductListingCMSRenderer<C extends AbstractCMSComponentModel> extends DefaultAddOnCMSComponentRenderer<C> {
    @Override
    public void renderComponent(final PageContext pageContext, final C component) throws ServletException, IOException
    {
        final Map<String, Object> exposedVariables = exposeVariables(pageContext, component);
        String pageView = "/WEB-INF/views/addons/affirmpaymentaddon/" + getUIExperienceFolder() + "/"
                + getCmsComponentFolder() + "/" + getViewResourceName(component) + ".jsp";
        pageContext.include(pageView);
        unExposeVariables(pageContext, component, exposedVariables);
    }
}
