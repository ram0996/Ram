/**
 *
 */
package com.shopping.facades.populators;

import de.hybris.platform.commercefacades.order.converters.populator.CartPopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.core.model.order.CartModel;

/**
 * @author venka
 *
 */
public class ShoppingCartPopulator extends CartPopulator
{
	@Override
	public void populate(final CartModel source, final CartData target)
	{
		super.populate(source, target);
		target.setMilitaryFlag(source.getMilitaryFlag());
	}
}
