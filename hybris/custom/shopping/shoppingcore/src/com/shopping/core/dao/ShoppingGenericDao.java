/**
 *
 */
package com.shopping.core.dao;

import com.shopping.core.model.ShoppingCountryConfigDataModel;


/**
 * @author venka
 *
 */
public interface ShoppingGenericDao
{
	boolean isCountryAndCurrencyPresent(String country, String currency);

	ShoppingCountryConfigDataModel getCountryConfigDetails(String country, String currency);
}
