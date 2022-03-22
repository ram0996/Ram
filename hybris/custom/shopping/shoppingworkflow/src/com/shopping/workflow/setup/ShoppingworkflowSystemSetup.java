/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.shopping.workflow.setup;

import static com.shopping.workflow.constants.ShoppingworkflowConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.shopping.workflow.constants.ShoppingworkflowConstants;
import com.shopping.workflow.service.ShoppingworkflowService;


@SystemSetup(extension = ShoppingworkflowConstants.EXTENSIONNAME)
public class ShoppingworkflowSystemSetup
{
	private final ShoppingworkflowService shoppingworkflowService;

	public ShoppingworkflowSystemSetup(final ShoppingworkflowService shoppingworkflowService)
	{
		this.shoppingworkflowService = shoppingworkflowService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		shoppingworkflowService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return ShoppingworkflowSystemSetup.class.getResourceAsStream("/shoppingworkflow/sap-hybris-platform.png");
	}
}
