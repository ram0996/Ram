/**
 *
 */
package com.shopping.facades.country.config.impl;

import java.util.Objects;

import javax.annotation.Resource;

import com.shopping.core.country.config.ShoppingCountryConfigService;
import com.shopping.core.model.ShoppingCountryConfigDataModel;
import com.shopping.facades.country.config.ShoppingCountryConfigFacade;
import com.shopping.facades.data.ShoppingCountryConfigData;
import com.shopping.facades.populators.ShoppingCountryPopulator;


/**
 * @author venka
 *
 */
public class ShoppingCountryConfigFacadeImpl implements ShoppingCountryConfigFacade
{
	@Resource(name = "shoppingCountryPopulator")
	private ShoppingCountryPopulator shoppingCountryPopulator;

	@Resource(name = "shoppingCountryConfigService")
	private ShoppingCountryConfigService shoppingCountryConfigService;

	@Override
	public ShoppingCountryConfigData getCountryConfigDetails(final String country, final String currency)
	{
		final ShoppingCountryConfigDataModel model = shoppingCountryConfigService.getCountryConfigDetails(country, currency);
		final ShoppingCountryConfigData data = new ShoppingCountryConfigData();

		if (Objects.nonNull(model))
		{
			shoppingCountryPopulator.populate(model, data);
		}
		return data;
	}

}
