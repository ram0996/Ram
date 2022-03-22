package com.affirm.payment.converters.populators;

import com.affirm.payment.core.service.AffirmPaymentCoreService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.AffirmPaymentInfoModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AffirmPaymentOrderDataPopulatorTest {
    @Mock
    AffirmPaymentCoreService affirmPaymentCoreService;
    @Mock
    AbstractPopulatingConverter<AffirmPaymentInfoModel, CCPaymentInfoData> affirmPaymentInfoConverter;
    @InjectMocks
    AffirmPaymentOrderDataPopulator defaultAffirmPaymentOrderDataPopulator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void populate() {
        OrderModel source = mock(OrderModel.class);
        OrderData target = new OrderData();
        when(affirmPaymentCoreService.isAffirmOrder(source)).thenReturn(true);
        defaultAffirmPaymentOrderDataPopulator.populate(source, target);
        Assert.assertEquals(target.getPaymentMode(), AffirmPaymentCoreService.PAYMENT_MODE_AFFIRM);
    }


}