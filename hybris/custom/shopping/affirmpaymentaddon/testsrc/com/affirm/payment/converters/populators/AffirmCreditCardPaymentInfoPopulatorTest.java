package com.affirm.payment.converters.populators;

import com.affirm.payment.core.service.AffirmPaymentCoreService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AffirmCreditCardPaymentInfoPopulatorTest {
    @InjectMocks
    AffirmCreditCardPaymentInfoPopulator affirmCreditCardPaymentInfoPopulator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void populate() {
        CreditCardPaymentInfoModel creditCardInfo = new CreditCardPaymentInfoModel();
        creditCardInfo.setAffirmVCNId("vcnId");
        CCPaymentInfoData ccPaymentInfoData = new CCPaymentInfoData();
        affirmCreditCardPaymentInfoPopulator.populate(creditCardInfo, ccPaymentInfoData);
        Assert.assertNotNull(ccPaymentInfoData.getCardTypeData());
        Assert.assertEquals(ccPaymentInfoData.getCardTypeData().getName(), AffirmPaymentCoreService.PAYMENT_MODE_AFFIRM);
        Assert.assertEquals(ccPaymentInfoData.getCardTypeData().getCode(), AffirmPaymentCoreService.PAYMENT_MODE_AFFIRM);
    }
}