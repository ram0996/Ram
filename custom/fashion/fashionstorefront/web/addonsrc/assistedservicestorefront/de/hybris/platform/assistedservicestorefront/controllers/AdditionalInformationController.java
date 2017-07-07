/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.assistedservicestorefront.controllers;

import de.hybris.platform.assistedservicefacades.AssistedServiceFacade;
import de.hybris.platform.assistedservicefacades.customer360.AdditionalInformationFrameworkFacade;
import de.hybris.platform.assistedservicefacades.customer360.Fragment;
import de.hybris.platform.assistedservicestorefront.constants.AssistedservicestorefrontConstants;
import de.hybris.platform.util.Config;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 * Controller to AIF requests for ASM
 *
 */
@Controller
@RequestMapping(value = "/assisted-service-aif")
public class AdditionalInformationController
{

	private static final String CUSTOMER_360_PAGE = "addon:/assistedservicestorefront/pages/customer360";
	private static final String CUSTOMER_360_FRAGMENT_404_PAGE = "addon:/assistedservicestorefront/fragments/customer360/fragment404";
	private static final String CUSTOMER_360_SECTION = "addon:/assistedservicestorefront/pages/section";

	private static final String AIF_AJAX_TIMEOUT = "aif_ajax_timeout";

	@Resource(name = "assistedServiceFacade")
	private AssistedServiceFacade assistedServiceFacade;

	@Resource(name = "additionalInformationFrameworkFacade")
	private AdditionalInformationFrameworkFacade additionalInformationFrameworkFacade;

	/***
	 * returns list of sections we have configured to display them on frontend, sections are not loaded just section
	 * title
	 *
	 * @param model
	 * @return list of sections to display
	 */
	@RequestMapping(value = "/customer360", method = RequestMethod.GET)
	public String getCustomer360(final Model model)
	{
		model.addAttribute("sections", additionalInformationFrameworkFacade.getSections());
		return CUSTOMER_360_PAGE;
	}

	/***
	 * get the section's fragments and display fragments meta info only like title
	 *
	 * @param model
	 * @param sectionId
	 *           section id to retrieve fragments for
	 * @return section info along with its fragments
	 */
	@RequestMapping(value = "/customer360section", method = RequestMethod.GET)
	public String getCustomer360Section(final Model model, @RequestParam("sectionId") final String sectionId)
	{
		model.addAttribute("section", additionalInformationFrameworkFacade.getSection(sectionId));

		model.addAttribute(AIF_AJAX_TIMEOUT, Integer.valueOf(Config.getInt(AssistedservicestorefrontConstants.AIF_TIMEOUT,
				AssistedservicestorefrontConstants.AIF_DEFAULT_TIMEOUT)));

		return CUSTOMER_360_SECTION;
	}

	/**
	 * Method for getting fragment's details, data and JSP renderer
	 *
	 * @param model
	 * @param fragmentId
	 *           fragment id to load
	 * @param sectionId
	 *           the section that this fragment belongs to
	 * @param allRequestParams
	 *           all request parameters
	 * @return fragment with populated data and renderer
	 */
	@RequestMapping(value = "/customer360Fragment", method = RequestMethod.GET)
	public String getCustomer360Fragment(final Model model, @RequestParam("fragmentId") final String fragmentId,
			@RequestParam("sectionId") final String sectionId, @RequestParam final Map<String, String> allRequestParams)
	{
		final Fragment populatedFragment = additionalInformationFrameworkFacade.getFragment(sectionId, fragmentId,
				allRequestParams);

		if (populatedFragment == null)
		{
			return CUSTOMER_360_FRAGMENT_404_PAGE;
		}

		model.addAttribute("fragmentData", populatedFragment.getData());

		return populatedFragment.getJspPath();
	}
}
