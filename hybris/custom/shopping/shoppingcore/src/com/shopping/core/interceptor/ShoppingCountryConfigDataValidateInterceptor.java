/**
 *
 */
package com.shopping.core.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.shopping.core.dao.ShoppingGenericDao;
import com.shopping.core.model.ShoppingCountryConfigDataModel;


/**
 * @author venka
 *
 */
public class ShoppingCountryConfigDataValidateInterceptor implements ValidateInterceptor<ShoppingCountryConfigDataModel>
{

	@Resource(name = "shoppingGenericDao")
	private ShoppingGenericDao shoppingGenericDao;

	@Override
	public void onValidate(final ShoppingCountryConfigDataModel model, final InterceptorContext ctx) throws InterceptorException
	{
		if (StringUtils.isNotBlank(model.getCountry()) && StringUtils.isNotBlank(model.getCurrency()))
		{
			/*if (shoppingGenericDao.isCountryAndCurrencyPresent(model.getCountry().trim(), model.getCurrency().trim()))
			{
				throw new InterceptorException("Country and currency combination already exists!");
			}*/
		}
		else
		{
			throw new InterceptorException("Country and currency should not be an empty!");
		}
	}

}
