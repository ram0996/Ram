/**
 *
 */
package com.shopping.core.dao.impl;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;

import com.shopping.core.dao.ShoppingGenericDao;
import com.shopping.core.model.ShoppingCountryConfigDataModel;

/**
 * @author venka
 *
 */
public class ShoppingGenericDaoImpl implements ShoppingGenericDao
{
	private final String COUNTRY_CURRENCY_COMBINATION = "SELECT {" + ShoppingCountryConfigDataModel.PK + "} FROM {"
			+ ShoppingCountryConfigDataModel._TYPECODE + "} WHERE {" + ShoppingCountryConfigDataModel.COUNTRY + "}=?country AND {"
			+ ShoppingCountryConfigDataModel.CURRENCY + "}=?currency";
	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Override
	public boolean isCountryAndCurrencyPresent(final String country, final String currency)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(COUNTRY_CURRENCY_COMBINATION);
		query.addQueryParameter("country", country);
		query.addQueryParameter("currency", currency);
		final SearchResult<MediaContainerModel> result = flexibleSearchService.search(query);
		if (Objects.nonNull(result) && CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0) != null ? true : false;
		}
		return false;
	}

	@Override
	public ShoppingCountryConfigDataModel getCountryConfigDetails(final String country, final String currency)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(COUNTRY_CURRENCY_COMBINATION);
		query.addQueryParameter("country", country.trim());
		query.addQueryParameter("currency", currency.trim());
		final SearchResult<ShoppingCountryConfigDataModel> result = flexibleSearchService.search(query);
		if (Objects.nonNull(result) && CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

}
