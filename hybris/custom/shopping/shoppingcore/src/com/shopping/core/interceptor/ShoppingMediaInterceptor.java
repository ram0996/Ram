/**
 * 
 */
package com.shopping.core.interceptor;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.mediaconversion.MediaConversionService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;

import com.shopping.core.service.ShoppingMediaService;

/**
 * @author venka
 *
 */
public class ShoppingMediaInterceptor implements PrepareInterceptor<MediaContainerModel>
{
	@Resource(name="mediaConversionService")
	private MediaConversionService mediaConversionService;
	
	@Override
	public void onPrepare(MediaContainerModel mediaContainer, InterceptorContext ctx) throws InterceptorException
	{
		if(Objects.nonNull(mediaContainer) && CollectionUtils.isNotEmpty(mediaContainer.getMedias()))
		{
			if(mediaContainer.getMedias().size() == 1)
			{
				mediaConversionService.convertMedias(mediaContainer);
			}
		}
	}

}
