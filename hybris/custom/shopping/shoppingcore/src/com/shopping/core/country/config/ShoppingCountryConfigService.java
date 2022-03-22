/**
 *
 */
package com.shopping.core.country.config;

import com.shopping.core.model.ShoppingCountryConfigDataModel;


/**
 * @author venka
 *
 */
public interface ShoppingCountryConfigService
{
	ShoppingCountryConfigDataModel getCountryConfigDetails(String country, String currency);
}
