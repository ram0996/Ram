/**
 *
 */
package com.shopping.core.country.config.impl;

import javax.annotation.Resource;

import com.shopping.core.country.config.ShoppingCountryConfigService;
import com.shopping.core.dao.ShoppingGenericDao;
import com.shopping.core.model.ShoppingCountryConfigDataModel;


/**
 * @author venka
 *
 */
public class ShoppingCountryConfigServiceImpl implements ShoppingCountryConfigService
{
	@Resource(name = "shoppingGenericDao")
	private ShoppingGenericDao shoppingGenericDao;


	@Override
	public ShoppingCountryConfigDataModel getCountryConfigDetails(final String country, final String currency)
	{
		return shoppingGenericDao.getCountryConfigDetails(country, currency);
	}

}
