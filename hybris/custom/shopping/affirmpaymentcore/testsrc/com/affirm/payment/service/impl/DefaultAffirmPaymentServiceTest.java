package com.affirm.payment.service.impl;

import com.affirm.checkout.pojo.AffirmLoanUpdateRequestData;
import com.affirm.payment.core.service.AffirmPaymentCoreService;
import com.affirm.payment.core.service.AffirmPaymentTransactionStrategy;
import com.affirm.payment.exception.AffirmPaymentException;
import com.affirm.payment.model.AffirmConfigContainerModel;
import com.affirm.payment.model.AffirmPaymentTransactionEntryModel;
import com.affirm.payment.service.executor.AffirmPaymentServiceExecutor;
import com.affirm.payment.service.executor.request.AffirmPaymentServiceRequest;
import com.affirm.payment.service.executor.request.builder.AffirmAuthorisationPaymentRequestServiceBuilder;
import com.affirm.payment.service.executor.result.AffirmPaymentServiceResult;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.AffirmPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAffirmPaymentServiceTest {
    @Mock
    AffirmPaymentCoreService affirmPaymentCoreService;
    @Mock
    ModelService modelService;
    @Mock
    AffirmPaymentTransactionStrategy affirmPaymentTransactionStrategy;
    @Mock
    AffirmPaymentServiceExecutor affirmPaymentServiceExecutor;
    @Mock
    AffirmPaymentInfoModel paymentInfo;
    @Mock
    CartModel cart;
    @Mock
    AddressModel address;
    @Mock
    DefaultAffirmPaymentService spyDefaultAffirmPaymentService;
    @Mock
    AffirmAuthorisationPaymentRequestServiceBuilder spyAffirmAuthPaymentServiceBuilder;
    @Mock
    AffirmPaymentServiceResult paymentServiceResult;
    @Mock
    PaymentTransactionEntryModel transactionEntry;
    @Mock
    PaymentTransactionModel paymentTransactionModel;
    @Mock
    AffirmPaymentTransactionEntryModel affirmPaymentTransactionEntryModel;
    @Mock
    AffirmPaymentTransactionEntryModel authorization;
    @Mock
    AffirmPaymentTransactionEntryModel captureEntry;
    @Mock
    PaymentModeModel paymentMode;
    @Mock
    AbstractOrderModel order;
    @Mock
    OrderModel orderModel;
    @InjectMocks
    DefaultAffirmPaymentService defaultAffirmPaymentService;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void initizePayment() {
        AffirmPaymentInfoModel paymentInfo = mock(AffirmPaymentInfoModel.class);
        when(affirmPaymentTransactionStrategy.createPaymentInfo(AffirmPaymentInfoModel.class, cart.getPaymentMode(), cart)).thenReturn(paymentInfo);
        defaultAffirmPaymentService.initizePayment(cart, address);
        verify(modelService).saveAll(address, paymentInfo, cart);
    }

    @Test
    public void authorisePayment() {
        String checkoutToken = "tokenString";
        PaymentTransactionEntryModel transaction = mock(PaymentTransactionEntryModel.class);
        AffirmConfigContainerModel config = mock(AffirmConfigContainerModel.class);
        when(affirmPaymentTransactionStrategy.getOrCreateTransaction(cart)).thenReturn(paymentTransactionModel);
        when(affirmPaymentTransactionStrategy.createTransactionEntryModel(
                AffirmPaymentTransactionEntryModel.class, checkoutToken, PaymentTransactionType.INITIATE, paymentTransactionModel)).thenReturn(affirmPaymentTransactionEntryModel);
        when(affirmPaymentCoreService.getPaymentMode()).thenReturn(paymentMode);
        PowerMockito.when(affirmPaymentServiceExecutor.execute(Mockito.any(AffirmPaymentServiceRequest.class))).thenReturn(paymentServiceResult);
        when(paymentServiceResult.getTransaction()).thenReturn(transaction);
        when(affirmPaymentTransactionStrategy.isTransactionSuccessfull(transaction)).thenReturn(true);
        when(affirmPaymentCoreService.getPaymentConfiguration(cart)).thenReturn(config);
        Assert.assertTrue(defaultAffirmPaymentService.authorisePayment(cart, checkoutToken));
        verify(affirmPaymentCoreService).getPaymentConfiguration(cart);
    }

    private AffirmAuthorisationPaymentRequestServiceBuilder newBuilderMock(CartModel cart, String checkoutToken) {
        AffirmAuthorisationPaymentRequestServiceBuilder b = mock(AffirmAuthorisationPaymentRequestServiceBuilder.class);
        when(b.setOrder(cart)).thenReturn(b);
        when(b.setCheckoutToken(checkoutToken)).thenReturn(b);
        when(b.setTransactionType(PaymentTransactionType.AUTHORIZATION)).thenReturn(b);
        return b;
    }

    @Test
    public void capturePayment() {
        PaymentTransactionEntryModel transaction = mock(PaymentTransactionEntryModel.class);
        when(affirmPaymentTransactionStrategy.getAcceptedTransaction(order, PaymentTransactionType.AUTHORIZATION)).thenReturn(authorization);
        HashMap<String, String> propsMap = new HashMap<String, String>();
        propsMap.put("id", "id");
        when(authorization.getProperties()).thenReturn(propsMap);
        PowerMockito.when(affirmPaymentServiceExecutor.execute(Mockito.any(AffirmPaymentServiceRequest.class))).thenReturn(paymentServiceResult);
        when(paymentServiceResult.getTransaction()).thenReturn(transaction);
        when(affirmPaymentTransactionStrategy.isTransactionSuccessfull(transaction)).thenReturn(true);
        Assert.assertTrue(defaultAffirmPaymentService.capturePayment(order));
        verify(affirmPaymentTransactionStrategy).getAcceptedTransaction(order, PaymentTransactionType.AUTHORIZATION);
    }

    @Test
    public void voidPayment() {
        PaymentTransactionEntryModel transaction = mock(PaymentTransactionEntryModel.class);
        when(affirmPaymentTransactionStrategy.getAcceptedTransaction(order, PaymentTransactionType.AUTHORIZATION)).thenReturn(authorization);
        HashMap<String, String> propsMap = new HashMap<String, String>();
        propsMap.put("id", "id");
        when(authorization.getProperties()).thenReturn(propsMap);
        PowerMockito.when(affirmPaymentServiceExecutor.execute(Mockito.any(AffirmPaymentServiceRequest.class))).thenReturn(paymentServiceResult);
        when(paymentServiceResult.getTransaction()).thenReturn(transaction);
        when(affirmPaymentTransactionStrategy.isTransactionSuccessfull(transaction)).thenReturn(true);
        Assert.assertTrue(defaultAffirmPaymentService.voidPayment(order));
    }

    @Test
    public void refundOrder() {
        PaymentTransactionEntryModel transaction = mock(PaymentTransactionEntryModel.class);
        when(affirmPaymentTransactionStrategy.getAcceptedTransaction(order, PaymentTransactionType.AUTHORIZATION)).thenReturn(authorization);
        when(affirmPaymentTransactionStrategy.getAcceptedTransaction(order, PaymentTransactionType.CAPTURE)).thenReturn(captureEntry);
        HashMap<String, String> propsMap = new HashMap<String, String>();
        propsMap.put("id", "id");
        when(authorization.getProperties()).thenReturn(propsMap);
        PowerMockito.when(affirmPaymentServiceExecutor.execute(Mockito.any(AffirmPaymentServiceRequest.class))).thenReturn(paymentServiceResult);
        exceptionRule.expect(AffirmPaymentException.class);
        when(paymentServiceResult.getTransaction()).thenReturn(transaction);
        when(affirmPaymentTransactionStrategy.isTransactionSuccessfull(transaction)).thenReturn(true);
        Assert.assertTrue(defaultAffirmPaymentService.refundOrder(orderModel, new BigDecimal(100.00)));
        verify(affirmPaymentCoreService).isRefundPossible(orderModel, new BigDecimal(100.0));
    }

    @Test
    public void updateLoan() {
        PaymentTransactionEntryModel transaction = mock(PaymentTransactionEntryModel.class);
        exceptionRule.expect(AffirmPaymentException.class);
        AffirmLoanUpdateRequestData updateRequestData = new AffirmLoanUpdateRequestData();
        when(affirmPaymentTransactionStrategy.getAcceptedTransaction(order, PaymentTransactionType.AUTHORIZATION)).thenReturn(authorization);
        HashMap<String, String> propsMap = new HashMap<String, String>();
        propsMap.put("id", "id");
        when(authorization.getProperties()).thenReturn(propsMap);
        PowerMockito.when(affirmPaymentServiceExecutor.execute(Mockito.any(AffirmPaymentServiceRequest.class))).thenReturn(paymentServiceResult);
        when(paymentServiceResult.getTransaction()).thenReturn(transaction);
        when(affirmPaymentTransactionStrategy.isTransactionSuccessfull(transaction)).thenReturn(true);
        Assert.assertTrue(defaultAffirmPaymentService.updateLoan(orderModel, updateRequestData));
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

}