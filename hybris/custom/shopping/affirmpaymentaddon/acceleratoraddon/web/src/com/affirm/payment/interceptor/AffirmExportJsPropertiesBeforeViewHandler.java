/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.affirm.payment.interceptor;

import com.affirm.payment.constants.AffirmpaymentaddonWebConstants;
import com.affirm.payment.model.AffirmConfigContainerModel;
import de.hybris.platform.acceleratorservices.storefront.data.JavaScriptVariableData;
import de.hybris.platform.addonsupport.config.javascript.BeforeViewJsPropsHandlerAdaptee;
import de.hybris.platform.addonsupport.config.javascript.JavaScriptVariableDataFactory;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import org.springframework.ui.ModelMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class AffirmExportJsPropertiesBeforeViewHandler extends BeforeViewJsPropsHandlerAdaptee {
    public static final String CART_DATA = "cartData";
    public static final String ORDER_DATA = "orderData";
    @Resource(name = "cmsSiteService")
    private CMSSiteService cmsSiteService;

    @Override
    public String beforeViewJsProps(final HttpServletRequest request, final HttpServletResponse response, final ModelMap model,
                                    final String viewName) {
        final CMSSiteModel currentSite = cmsSiteService.getCurrentSite();
        Map<String, List<JavaScriptVariableData>> jsVariables = (Map<String, List<JavaScriptVariableData>>) model
                .get(detectJsModelName());

        AffirmConfigContainerModel affirmConfigContainer = currentSite.getAffirmConfigContainer();
        if(affirmConfigContainer == null){
           return viewName;
        }
        String affirmPublicKey = affirmConfigContainer.getAffirmPublicKey();
        String affirmSiteUrl = affirmConfigContainer.getAffirmScriptUrl();

        List<JavaScriptVariableData> javaScriptVariableData = jsVariables.get(super.getMessageSource().getAddOnName());

        javaScriptVariableData.add(JavaScriptVariableDataFactory.create(AffirmpaymentaddonWebConstants.AFFIRM_PUBLIC_KEY, affirmPublicKey));
        javaScriptVariableData.add(JavaScriptVariableDataFactory.create(AffirmpaymentaddonWebConstants.AFFIRM_SITE_URL, affirmSiteUrl));

        String sessionId = "";
        if(model.get(CART_DATA) instanceof CartData){
            sessionId = ((CartData)model.get(CART_DATA)).getGuid();
        }else if(model.get(ORDER_DATA) instanceof OrderData){
           sessionId = ((OrderData)model.get(ORDER_DATA)).getGuid();
        }
        javaScriptVariableData.add(JavaScriptVariableDataFactory.create(AffirmpaymentaddonWebConstants.AFFIRM_SESSION_ID, sessionId));


        return viewName;
    }
}
