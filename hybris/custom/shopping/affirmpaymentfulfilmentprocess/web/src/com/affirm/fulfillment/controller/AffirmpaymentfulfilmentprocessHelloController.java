/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.affirm.fulfillment.controller;

import static com.affirm.fulfillment.constants.AffirmpaymentfulfilmentprocessConstants.PLATFORM_LOGO_CODE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.affirm.fulfillment.service.AffirmpaymentfulfilmentprocessService;


@Controller
public class AffirmpaymentfulfilmentprocessHelloController
{
	@Autowired
	private AffirmpaymentfulfilmentprocessService affirmpaymentfulfilmentprocessService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome(final ModelMap model)
	{
		model.addAttribute("logoUrl", affirmpaymentfulfilmentprocessService.getHybrisLogoUrl(PLATFORM_LOGO_CODE));
		return "welcome";
	}
}
