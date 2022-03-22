package com.affirm.payment.facade.impl;

import com.affirm.checkout.pojo.AffirmLoanUpdateRequestData;
import com.affirm.payment.core.constants.GeneratedAffirmpaymentcoreConstants;
import com.affirm.payment.core.service.AffirmPaymentCoreService;
import com.affirm.payment.model.AffirmConfigContainerModel;
import com.affirm.payment.service.AffirmPaymentService;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.jalo.order.payment.PaymentMode;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.Resource;
import javax.validation.constraints.AssertTrue;

import java.math.BigDecimal;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAffirmPaymentFacadeTest {
    @Mock
    CartService cartService;
    @Mock
    CartModel cart;
    @Mock
    CMSSiteService cmsSiteService;
    @Mock
    Converter<AddressData, AddressModel> addressReverseConverter;
    @Mock
    AffirmPaymentService affirmPaymentService;
    @Mock
    AffirmPaymentCoreService affirmPaymentCoreService;
    @Mock
    AddressData addressData;
    @Mock
    AddressModel addressModel;
    @Mock
    AbstractOrderModel order;
    @Mock
    OrderModel orderModel;
    @Mock
    CMSSiteModel siteModel;
    @Mock
    AffirmConfigContainerModel affirmConfigContainerModel;
    @Mock
    AffirmLoanUpdateRequestData affirmLoanUpdateRequestData;
    @Mock
    PaymentModeModel paymentMode;
    @Mock
    OrderData orderData;
    @InjectMocks
    DefaultAffirmPaymentFacade defaultAffirmPaymentFacade;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void initizePayment() {
        when(cartService.hasSessionCart()).thenReturn(true);
        when(addressReverseConverter.convert(addressData)).thenReturn(addressModel);
        when(cartService.getSessionCart()).thenReturn(cart);
        defaultAffirmPaymentFacade.initizePayment(addressData);
        verify(affirmPaymentService).initizePayment(cart, addressModel);

    }

    @Test
    public void authorisePayment() {
        String token = "token";
        when(cartService.hasSessionCart()).thenReturn(true);
        when(cartService.getSessionCart()).thenReturn(cart);
        defaultAffirmPaymentFacade.authorisePayment(token);
        verify(affirmPaymentService).authorisePayment(cart,token);
    }

    @Test
    public void capturePayment() {
        String orderId = "order100";
       when(affirmPaymentCoreService.getOrderByCode(orderId)).thenReturn(order);
       defaultAffirmPaymentFacade.capturePayment(orderId);
       verify(affirmPaymentService).capturePayment(order);
    }

    @Test
    public void voidPayment() {
        String orderId = "order100";
        when(affirmPaymentCoreService.getOrderByCode(orderId)).thenReturn(order);
        defaultAffirmPaymentFacade.voidPayment(orderId);
        verify(affirmPaymentService).voidPayment(order);

    }

    @Test
    public void isAffirmPaymentEnabled() {

        affirmConfigContainerModel.setEnabled(true);
        when(cmsSiteService.getCurrentSite()).thenReturn(siteModel);
        siteModel.setAffirmConfigContainer(affirmConfigContainerModel);
        affirmConfigContainerModel.setEnabled(true);
        when(siteModel.getAffirmConfigContainer()).thenReturn(affirmConfigContainerModel);
        defaultAffirmPaymentFacade.isAffirmPaymentEnabled();
        verify(siteModel).getAffirmConfigContainer();

    }

    @Test
    public void refundOrder() {
        String orderId = "order100";
        when(affirmPaymentCoreService.getOrderByCode(orderId)).thenReturn(orderModel);
        defaultAffirmPaymentFacade.refundOrder(orderId,new BigDecimal(100.00));
        verify(affirmPaymentService).refundOrder(orderModel,new BigDecimal(100.00));

    }

    @Test
    public void updateLoan() {
        String orderId = "order100";
        when(affirmPaymentCoreService.getOrderByCode(orderId)).thenReturn(orderModel);
        defaultAffirmPaymentFacade.updateLoan(orderId,affirmLoanUpdateRequestData);
        verify(affirmPaymentService).updateLoan(orderModel,affirmLoanUpdateRequestData);

    }

    @Test
    public void isAffirmPaymentEnabledForCurrentCart() {
        when(cartService.getSessionCart()).thenReturn(cart);
        when(affirmPaymentCoreService.getPaymentMode()).thenReturn(paymentMode);
        defaultAffirmPaymentFacade.isAffirmPaymentEnabledForCurrentCart();
        verify(affirmPaymentCoreService).getPaymentMode();

    }

    @Test
    public void isAffirmOrder() {
        when(orderData.getPaymentMode()).thenReturn(affirmPaymentCoreService.PAYMENT_MODE_AFFIRM);
        boolean isAffirm = defaultAffirmPaymentFacade.isAffirmOrder(orderData);
        Assert.assertTrue(isAffirm);

    }
}