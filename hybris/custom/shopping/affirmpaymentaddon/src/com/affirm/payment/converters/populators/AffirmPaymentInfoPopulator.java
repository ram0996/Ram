package com.affirm.payment.converters.populators;

import com.affirm.payment.core.service.AffirmPaymentCoreService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.AffirmPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

public class AffirmPaymentInfoPopulator implements Populator<AffirmPaymentInfoModel, CCPaymentInfoData> {
   private Converter<AddressModel, AddressData> addressConverter;

   @Override public void populate(AffirmPaymentInfoModel source, CCPaymentInfoData target) throws ConversionException {
      target.setId(source.getPk().toString());
      target.setSaved(source.isSaved());
      if (source.getBillingAddress() != null)
      {
         target.setBillingAddress(addressConverter.convert(source.getBillingAddress()));
      }

      final CardTypeData cardTypeData = new CardTypeData();
      cardTypeData.setName(AffirmPaymentCoreService.PAYMENT_MODE_AFFIRM);
      cardTypeData.setCode(AffirmPaymentCoreService.PAYMENT_MODE_AFFIRM);
      target.setCardTypeData(cardTypeData);

   }

   @Required public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter) {
      this.addressConverter = addressConverter;
   }

}
