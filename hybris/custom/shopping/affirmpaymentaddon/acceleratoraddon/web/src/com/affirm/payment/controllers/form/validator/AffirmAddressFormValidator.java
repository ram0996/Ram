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
package com.affirm.payment.controllers.form.validator;

import com.affirm.payment.controllers.form.AffirmAddressForm;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component("affirmAddressFormValidator")
public class AffirmAddressFormValidator implements Validator
{
	private static final String PAYMENT_START_DATE_INVALID = "payment.startDate.invalid";

	@Override
	public boolean supports(final Class<?> aClass)
	{
		return AffirmAddressForm.class.equals(aClass);
	}

	@Override
	public void validate(final Object object, final Errors errors)
	{
		final AffirmAddressForm form = (AffirmAddressForm) object;
		if (StringUtils.isBlank(form.getBillTo_country()))
		{
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_country", "address.country.invalid");
		}
		else
		{
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_firstName", "address.firstName.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_lastName", "address.lastName.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_street1", "address.line1.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_city", "address.city.invalid");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "billTo_postalCode", "address.postcode.invalid");
		}
	}
}
