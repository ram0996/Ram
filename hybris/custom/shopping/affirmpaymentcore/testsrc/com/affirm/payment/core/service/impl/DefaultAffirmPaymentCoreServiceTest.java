package com.affirm.payment.core.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.AffirmPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;

import com.affirm.payment.core.service.AffirmPaymentTransactionStrategy;
import com.affirm.payment.dao.AffirmOrderDAO;
import com.affirm.payment.model.AffirmConfigContainerModel;
import com.affirm.payment.model.PaymentThresholdModel;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAffirmPaymentCoreServiceTest {
    @Mock
    MediaService mediaService;
    @Mock
    ModelService modelService;
    @Mock
    FlexibleSearchService flexibleSearchService;
    @Mock
    MediaModel mediaModel;
    @Mock
    AffirmOrderDAO affirmOrderDAO;
    @Mock
    AffirmPaymentTransactionStrategy affirmPaymentTransactionStrategy;
    @Mock
    CatalogUnawareMediaModel media;
    @Mock
    CartModel cart;
    @Mock
    CMSSiteModel cmsite;
    @Mock
    AddressModel address;
    @Mock
    PaymentModeModel paymentModel;


    DefaultAffirmPaymentCoreService defaultAffirmPaymentCoreServiceSpy;
    @InjectMocks
    DefaultAffirmPaymentCoreService defaultAffirmPaymentCoreService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        defaultAffirmPaymentCoreServiceSpy = PowerMockito.spy(new DefaultAffirmPaymentCoreService());
    }

    @Test
    public void getHybrisLogoUrl() {
        final String logoCode = "affirmpaymentcorePlatformLogo";
        when(mediaService.getMedia(logoCode)).thenReturn(mediaModel);
        defaultAffirmPaymentCoreService.getHybrisLogoUrl(logoCode);
        verify(mediaService).getMedia(logoCode);
    }


    @Test
    public void getPaymentConfiguration() {
        AffirmConfigContainerModel affirmConfigContainerModel = new AffirmConfigContainerModel();
        affirmConfigContainerModel.setAffirmContainerCode("newAffirmContainer");
        when(cart.getSite()).thenReturn(cmsite);
        when(cmsite.getAffirmConfigContainer()).thenReturn(affirmConfigContainerModel);
        Assert.assertNotNull(defaultAffirmPaymentCoreService.getPaymentConfiguration(cart));
    }

    @Test
    public void getPaymentMode() {
        when(defaultAffirmPaymentCoreService.getPaymentMode()).thenReturn(paymentModel);
        Assert.assertNotNull(paymentModel);
    }

    @Test
    public void getOrderByCode() {
        String orderId = "testOrder";
        defaultAffirmPaymentCoreService.getOrderByCode(orderId);
        verify(affirmOrderDAO).getOrderByCode(orderId);
    }

    @Test
    public void isRefundPossible() {
        BigDecimal refundAmount = new BigDecimal(150);
        OrderModel order = mock(OrderModel.class);
        when(order.getTotalIncludingTax()).thenReturn(10.00);
        when(affirmPaymentTransactionStrategy.getRefundedAmount(order)).thenReturn(refundAmount);
        Assert.assertTrue(defaultAffirmPaymentCoreService.isRefundPossible(order, new BigDecimal(120)));
        Assert.assertTrue(defaultAffirmPaymentCoreService.isRefundPossible(order, null));
        Assert.assertFalse(defaultAffirmPaymentCoreService.isRefundPossible(order, new BigDecimal(1000)));
    }

    @Test
    public void isAffirmOrder() {
        OrderModel order = mock(OrderModel.class);
        AffirmPaymentInfoModel affirmPaymentInfo = mock(AffirmPaymentInfoModel.class);
        when(order.getPaymentInfo()).thenReturn(affirmPaymentInfo);
        Assert.assertTrue(defaultAffirmPaymentCoreService.isAffirmOrder(order));
    }

    @Test
    public void isVCNOrder() {
        OrderModel order = mock(OrderModel.class);
        CreditCardPaymentInfoModel creditCardPaymentInfo = mock(CreditCardPaymentInfoModel.class);
        when(order.getPaymentInfo()).thenReturn(creditCardPaymentInfo);
        when(creditCardPaymentInfo.getAffirmVCNId()).thenReturn("vcnId");
        Assert.assertTrue(defaultAffirmPaymentCoreService.isVCNOrder(order));
    }

    @Test
    public void isPaymentModeSupported() {
        Collection<PaymentThresholdModel> thresholds = new ArrayList<PaymentThresholdModel>() {
            @Override
            public boolean isEmpty() {
                return true;
            }
        };
        when(paymentModel.getThresholds()).thenReturn(thresholds);
        Assert.assertTrue(defaultAffirmPaymentCoreService.isPaymentModeSupported(cart, paymentModel));
    }
}