package com.affirm.payment.converters.populators;

import com.affirm.payment.core.service.AffirmPaymentCoreService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.common.functions.Converter;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.order.payment.AffirmPaymentInfoModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AffirmPaymentInfoPopulatorTest {

    @Mock
    Converter addressConvertor;

    @InjectMocks
    AffirmPaymentInfoPopulator affirmPaymentInfoPopulator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void populate() {
        AffirmPaymentInfoModel source = mock(AffirmPaymentInfoModel.class);
        CCPaymentInfoData target = new CCPaymentInfoData();
        PK pk = PK.parse("42");
        doReturn(pk).when(source).getPk();
        affirmPaymentInfoPopulator.populate(source, target);
        Assert.assertNotNull(target.getCardTypeData());
        Assert.assertEquals(target.getCardTypeData().getName(), AffirmPaymentCoreService.PAYMENT_MODE_AFFIRM);
        Assert.assertEquals(target.getCardTypeData().getCode(), AffirmPaymentCoreService.PAYMENT_MODE_AFFIRM);
    }
}