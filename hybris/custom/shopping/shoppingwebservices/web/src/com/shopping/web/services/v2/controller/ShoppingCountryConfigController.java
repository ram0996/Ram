/**
 *
 */
package com.shopping.web.services.v2.controller;

import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;

import java.util.Objects;

import javax.annotation.Resource;
import javax.ws.rs.PathParam;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shopping.core.country.config.ShoppingCountryConfigService;
import com.shopping.core.model.ShoppingCountryConfigDataModel;
import com.shopping.facades.country.config.ShoppingCountryConfigFacade;
import com.shopping.facades.data.ShoppingCountryConfigData;
import com.shopping.web.services.data.shoppingCountryConfigWsDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


/**
 * @author venka
 *
 */

@Controller
@RequestMapping(value = "/{baseSiteId}/country")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@Api(tags = "ShoppingCountryConfigData")
public class ShoppingCountryConfigController extends BaseCommerceController
{
	private static final Logger LOG = LoggerFactory.getLogger(ShoppingCountryConfigController.class);

	@Resource(name = "shoppingCountryConfigFacade")
	private ShoppingCountryConfigFacade shoppingCountryConfigFacade;

	@Resource(name = "shoppingCountryConfigService")
	private ShoppingCountryConfigService shoppingCountryConfigService;

	@Resource(name = "modelService")
	private ModelService modelService;


	@GetMapping(value = "/{country}/currency/{currency}")
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT" })
	@ResponseBody
	@ApiOperation(nickname = "getCountryConfigDetails", value = "Get Country and Currency Details.", notes = "Lists all customer carts.")
	@ApiBaseSiteIdAndUserIdParam
	public shoppingCountryConfigWsDTO getCountryConfigDetails(@RequestParam(defaultValue = DEFAULT_FIELD_SET)
	final String fields, @PathVariable
	final String country, @PathVariable
	final String currency)
	{
		final ShoppingCountryConfigData data = shoppingCountryConfigFacade.getCountryConfigDetails(country, currency);

		return getDataMapper().map(data, shoppingCountryConfigWsDTO.class, fields);
	}

	@RequestMapping(value = "/{country}/currency/{currency}", method = RequestMethod.DELETE)
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT" })
	@ResponseBody
	@ApiOperation(nickname = "deleteCountryConfigData", value = "DELETE Country and Currency Details.", notes = "DELETE Mentioned Country and Currency Details.")
	@ApiBaseSiteIdAndUserIdParam
	public String deleteCountryConfigData(@PathVariable
	final String country, @PathVariable
	final String currency)
	{
		final ShoppingCountryConfigDataModel model = shoppingCountryConfigService.getCountryConfigDetails(country, currency);
		modelService.remove(model);
		return "Success";
	}

	@RequestMapping(value = "/createCountry", method = RequestMethod.POST)
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT" })
	@ResponseBody
	@ApiOperation(nickname = "createCountryConfigData", value = "Create Country and Currency Details.", notes = "Create new Country.")
	@ApiBaseSiteIdAndUserIdParam
	public shoppingCountryConfigWsDTO createCountryConfigData(@RequestBody
	final shoppingCountryConfigWsDTO wsDTO)
	{

		final ShoppingCountryConfigDataModel model = modelService.create(ShoppingCountryConfigDataModel.class);
		model.setCountry(wsDTO.getCountry());
		model.setCurrency(wsDTO.getCurrency());
		model.setPartRestriction(wsDTO.getPartRestriction());
		modelService.save(model);
		return wsDTO;
	}

	@RequestMapping(value = "/updateCountry/{country}/currency/{currency}", method = RequestMethod.PUT)
	@Secured(
	{ "ROLE_CUSTOMERGROUP", "ROLE_CUSTOMERMANAGERGROUP", "ROLE_GUEST", "ROLE_TRUSTED_CLIENT" })
	@ResponseBody
	@ApiOperation(nickname = "updateCountry", value = "update Country and Currency Details.", notes = "Update Country Details.")
	@ApiBaseSiteIdAndUserIdParam
	public shoppingCountryConfigWsDTO updateCountryConfigData(@PathVariable final String country, @PathVariable final String currency, @PathParam(value = "partRestriction") final String partRestriction)
	{
		final ShoppingCountryConfigDataModel model = shoppingCountryConfigService.getCountryConfigDetails(country, currency);
		if (Objects.nonNull(model))
		{
			if (StringUtils.isBlank(model.getPartRestriction()))
			{
				model.setPartRestriction(partRestriction);
			}
			else
			{
				final String part = model.getPartRestriction();
				model.setPartRestriction(part + "," + partRestriction);
			}
			modelService.save(model);
		}

		final ShoppingCountryConfigData data = shoppingCountryConfigFacade.getCountryConfigDetails(country, currency);

		return getDataMapper().map(data, shoppingCountryConfigWsDTO.class);

	}

}
