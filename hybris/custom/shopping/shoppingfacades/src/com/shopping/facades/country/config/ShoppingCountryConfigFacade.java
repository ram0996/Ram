/**
 *
 */
package com.shopping.facades.country.config;

import com.shopping.facades.data.ShoppingCountryConfigData;


/**
 * @author venka
 *
 */
public interface ShoppingCountryConfigFacade
{
	ShoppingCountryConfigData getCountryConfigDetails(String country, String currency);
}
