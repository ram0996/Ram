/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.affirm.fulfillment.service;

public interface AffirmpaymentfulfilmentprocessService
{
	String getHybrisLogoUrl(String logoCode);

	void createLogo(String logoCode);
}
