/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.affirm.payment.core.setup;

import static com.affirm.payment.core.constants.AffirmpaymentcoreConstants.PLATFORM_LOGO_CODE;

import com.affirm.payment.core.service.AffirmPaymentCoreService;
import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.affirm.payment.core.constants.AffirmpaymentcoreConstants;

@SystemSetup(extension = AffirmpaymentcoreConstants.EXTENSIONNAME)
public class AffirmpaymentcoreSystemSetup
{
	private final AffirmPaymentCoreService affirmPaymentCoreService;

	public AffirmpaymentcoreSystemSetup(final AffirmPaymentCoreService affirmPaymentCoreService)
	{
		this.affirmPaymentCoreService = affirmPaymentCoreService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		affirmPaymentCoreService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return AffirmpaymentcoreSystemSetup.class.getResourceAsStream("/affirmpaymentcore/sap-hybris-platform.png");
	}
}
