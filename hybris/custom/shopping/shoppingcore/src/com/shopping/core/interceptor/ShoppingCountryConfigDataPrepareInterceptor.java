package com.shopping.core.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import org.apache.commons.lang3.StringUtils;

import com.shopping.core.model.ShoppingCountryConfigDataModel;


/**
 *
 */

/**
 * @author venka
 *
 */
public class ShoppingCountryConfigDataPrepareInterceptor implements PrepareInterceptor<ShoppingCountryConfigDataModel>
{

	@Override
	public void onPrepare(final ShoppingCountryConfigDataModel model, final InterceptorContext ctx) throws InterceptorException
	{
		if (StringUtils.isBlank(model.getCountry()))
		{
			throw new InterceptorException("Country and Currency should not be null");
		}
	}

}
