package com.affirm.payment.converters.populators;

import com.affirm.payment.core.service.AffirmPaymentCoreService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import org.apache.commons.lang3.StringUtils;

public class AffirmCreditCardPaymentInfoPopulator implements Populator<CreditCardPaymentInfoModel, CCPaymentInfoData>
{


   @Override
   public void populate(final CreditCardPaymentInfoModel source, final CCPaymentInfoData target)
   {
      target.setAffirmVCNId(source.getAffirmVCNId());
      if(StringUtils.isNotEmpty(source.getAffirmVCNId())) {
         final CardTypeData cardTypeData = new CardTypeData();
         cardTypeData.setName(AffirmPaymentCoreService.PAYMENT_MODE_AFFIRM);
         cardTypeData.setCode(AffirmPaymentCoreService.PAYMENT_MODE_AFFIRM);
         target.setCardTypeData(cardTypeData);
         target.setCardNumber("");
      }
   }
}
