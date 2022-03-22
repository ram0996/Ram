/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.shopping.workflow.controller;

import static com.shopping.workflow.constants.ShoppingworkflowConstants.PLATFORM_LOGO_CODE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.shopping.workflow.service.ShoppingworkflowService;


@Controller
public class ShoppingworkflowHelloController
{
	@Autowired
	private ShoppingworkflowService shoppingworkflowService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome(final ModelMap model)
	{
		model.addAttribute("logoUrl", shoppingworkflowService.getHybrisLogoUrl(PLATFORM_LOGO_CODE));
		return "welcome";
	}
}
