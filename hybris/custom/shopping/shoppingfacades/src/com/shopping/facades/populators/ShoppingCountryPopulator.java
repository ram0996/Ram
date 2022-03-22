/**
 *
 */
package com.shopping.facades.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.shopping.core.model.ShoppingCountryConfigDataModel;
import com.shopping.facades.data.ShoppingCountryConfigData;

/**
 * @author venka
 *
 */
public class ShoppingCountryPopulator implements Populator<ShoppingCountryConfigDataModel, ShoppingCountryConfigData>
{

	@Override
	public void populate(final ShoppingCountryConfigDataModel source, final ShoppingCountryConfigData target)
			throws ConversionException
	{
		target.setCountry(source.getCountry());
		target.setCurrency(source.getCurrency());
		target.setPartRestriction(source.getPartRestriction());
	}

}
