/**
 *
 */
package com.shopping.core.service;

import de.hybris.platform.core.model.media.MediaContainerModel;



/**
 * @author venka
 *
 */
public interface ShoppingMediaService
{
	MediaContainerModel getMediaContainer(String code);

	boolean convertAssignedMedia(final MediaContainerModel mediaContainer);
}
