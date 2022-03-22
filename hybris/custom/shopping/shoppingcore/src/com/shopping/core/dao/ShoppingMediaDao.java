/**
 *
 */
package com.shopping.core.dao;

import de.hybris.platform.core.model.media.MediaContainerModel;



/**
 * @author venkat
 *
 */
public interface ShoppingMediaDao
{
	MediaContainerModel getMediaContainer(String code);
}
