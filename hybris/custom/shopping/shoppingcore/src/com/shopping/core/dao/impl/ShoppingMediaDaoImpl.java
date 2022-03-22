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

import com.shopping.core.dao.ShoppingMediaDao;


/**
 * @author venkat
 *
 */
public class ShoppingMediaDaoImpl implements ShoppingMediaDao
{
	private static final String MEDIA_CONATINER = "SELECT {" + MediaContainerModel.PK + "} FROM {" + MediaContainerModel._TYPECODE
			+ "} " + "WHERE {" + MediaContainerModel.QUALIFIER + "}=?code";

	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Override
	public MediaContainerModel getMediaContainer(final String code)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(MEDIA_CONATINER);
		query.addQueryParameter("code", code);
		final SearchResult<MediaContainerModel> result = flexibleSearchService.search(query);
		if (Objects.nonNull(result) && CollectionUtils.isNotEmpty(result.getResult()))
		{
			return result.getResult().get(0);
		}
		return null;
	}

}
