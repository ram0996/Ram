package com.affirm.payment.converter;

import com.affirm.payment.controllers.form.AffirmAddressForm;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.springframework.util.Assert;

import javax.annotation.Resource;

public class AffirmFormAddressDataPopulator  implements Populator<AffirmAddressForm, AddressData> {

   @Resource private CommonI18NService commonI18NService;

   @Override public void populate(AffirmAddressForm source, AddressData target) throws ConversionException {
      Assert.notNull(source, "Parameter source cannot be null.");
      Assert.notNull(target, "Parameter target cannot be null.");

      populateBasicFields(source, target);
      populateAddressFields(source, target);
      populateCountryAndRegion(source, target);
   }

   protected void populateBasicFields(final AffirmAddressForm source, final AddressData target)
   {
      target.setBillingAddress(Boolean.TRUE);
      target.setShippingAddress(Boolean.FALSE);
      target.setVisibleInAddressBook(Boolean.TRUE);
   }

   protected void populateAddressFields(final AffirmAddressForm source, final AddressData target)
   {
      if (source.getBillTo_titleCode() != null)
      {
         target.setTitleCode(source.getBillTo_titleCode());
      }
      target.setFirstName(source.getBillTo_firstName());
      target.setLastName(source.getBillTo_lastName());

      target.setLine1(source.getBillTo_street1());
      target.setLine2(source.getBillTo_street2());
      target.setTown(source.getBillTo_city());
      target.setPostalCode(source.getBillTo_postalCode());
      target.setPhone(source.getBillTo_phoneNumber());
      target.setEmail(source.getBillTo_email());
   }

   protected void populateCountryAndRegion(final AffirmAddressForm source, final AddressData target)
   {
      final CountryModel countryModel = commonI18NService.getCountry(source.getBillTo_country());
      final CountryData countryData = new CountryData();
      String countryIso = countryModel.getIsocode();
      countryData.setIsocode(countryIso);
      countryData.setName(countryModel.getName());
      target.setCountry(countryData);

      if(source.getBillTo_state() != null){
         final RegionData regionData = new RegionData();
         regionData.setIsocodeShort(source.getBillTo_state());
         regionData.setIsocode(countryIso + "-" + source.getBillTo_state());
         regionData.setCountryIso(countryIso);
         target.setRegion(regionData);
      }
   }

}
