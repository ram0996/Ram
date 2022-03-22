/**
 *
 */
package com.shopping.core.service.impl;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.mediaconversion.MediaConversionService;

import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import com.shopping.core.dao.ShoppingMediaDao;
import com.shopping.core.service.ShoppingMediaService;


/**
 * @author venkat
 *
 */
public class ShoppingMediaServiceImpl implements ShoppingMediaService
{
	@Resource(name = "shoppingMediaDao")
	private ShoppingMediaDao shoppingMediaDao;

	@Resource(name = "mediaConversionService")
	private MediaConversionService mediaConversionService;

	@Override
	public MediaContainerModel getMediaContainer(final String code)
	{
		return shoppingMediaDao.getMediaContainer(code);
	}

	public boolean convertAssignedMedia(final MediaContainerModel mediaContainer)
	{
		Assert.notNull(mediaContainer, "mediaContainerCode must not be null.");
		if (Objects.nonNull(mediaContainer))
		{
			if (CollectionUtils.isNotEmpty(mediaContainer.getMedias()) && mediaContainer.getMedias().size() > 1)
			{
				//Nothing to do
			}
			else
			{
				mediaConversionService.convertMedias(mediaContainer);
			}
		}

		return true;

	}


}
