package com.affirm.payment.core.service.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.AffirmPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentModeModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
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
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultAffirmPaymentTransactionStrategyTest {
    @Mock
    ModelService modelService;

    AffirmPaymentInfoModel affirmPaymentInfoModel;
    AbstractOrderModel cart;
    Class<AffirmPaymentInfoModel> paymentInfoModelClass;
    PaymentModeModel paymentModeModel;
    PaymentTransactionModel paymentTransactionModel;
    Class<PaymentTransactionModel> paymentTransactionModelClass;
    PaymentTransactionEntryModel paymentTransactionEntryModel;
    Class<PaymentTransactionEntryModel> paymentTransactionEntryModelClass;
    OrderModel order;

    @InjectMocks
    DefaultAffirmPaymentTransactionStrategy defaultAffirmPaymentTransactionStrategy;

    @Before
    public void setUp() throws Exception {
        affirmPaymentInfoModel = new AffirmPaymentInfoModel();
        cart = new AbstractOrderModel();
        order = new OrderModel();
        paymentInfoModelClass = AffirmPaymentInfoModel.class;
        cart.setCode("testCart");
        UserModel testUser = new CustomerModel();
        ((CustomerModel) testUser).setCustomerID("test");
        cart.setUser(testUser);
        paymentModeModel = new PaymentModeModel();
        paymentModeModel.setCode("affirm");
        cart.setPaymentMode(paymentModeModel);
        paymentTransactionModel = new PaymentTransactionModel();
        paymentTransactionModelClass = PaymentTransactionModel.class;
        paymentTransactionEntryModel = new PaymentTransactionEntryModel();
        paymentTransactionEntryModelClass = PaymentTransactionEntryModel.class;
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void createPaymentInfo() {
        when(modelService.create(paymentInfoModelClass)).thenReturn(affirmPaymentInfoModel);
        defaultAffirmPaymentTransactionStrategy.createPaymentInfo(paymentInfoModelClass, paymentModeModel, cart);
        Assert.assertEquals(affirmPaymentInfoModel.getCode(), paymentModeModel.getCode() + "_" + cart.getCode());
        Assert.assertEquals(affirmPaymentInfoModel.getUser(), cart.getUser());
    }

    @Test
    public void getTransaction() {
        List<PaymentTransactionModel> paymentTransactionModelList = new ArrayList<PaymentTransactionModel>();
        PaymentTransactionModel paymentTransaction = new PaymentTransactionModel();
        paymentTransaction.setPaymentProvider("affirm");
        paymentTransactionModelList.add(paymentTransaction);
        cart.setPaymentTransactions(paymentTransactionModelList);
        Assert.assertEquals(defaultAffirmPaymentTransactionStrategy.getTransaction(cart), paymentTransaction);
    }

    @Test
    public void getOrCreateTransaction() {
        when(modelService.create(paymentTransactionModelClass)).thenReturn(paymentTransactionModel);
        defaultAffirmPaymentTransactionStrategy.getOrCreateTransaction(cart);
        Assert.assertEquals(paymentTransactionModel.getRequestId(), cart.getCode());
        Assert.assertEquals(paymentTransactionModel.getRequestToken(), cart.getCode());
        Assert.assertEquals(paymentTransactionModel.getPaymentProvider(), cart.getPaymentMode().getCode());
        Assert.assertFalse(cart.getPaymentTransactions().isEmpty());
    }

    @Test
    public void createTransactionEntryModel() {
        when(modelService.create(paymentTransactionEntryModelClass)).thenReturn(paymentTransactionEntryModel);
        defaultAffirmPaymentTransactionStrategy.createTransactionEntryModel(paymentTransactionEntryModelClass, "test", PaymentTransactionType.AUTHORIZATION, paymentTransactionModel);
        Assert.assertEquals(paymentTransactionEntryModel.getType(), PaymentTransactionType.AUTHORIZATION);
        Assert.assertEquals(paymentTransactionEntryModel.getRequestId(), "test");
        Assert.assertEquals(paymentTransactionEntryModel.getRequestToken(), "test");
        Assert.assertNotNull(paymentTransactionEntryModel.getTime());
        Assert.assertEquals(paymentTransactionEntryModel.getTransactionStatus(), TransactionStatus.ACCEPTED.name());
        Assert.assertEquals(paymentTransactionEntryModel.getTransactionStatusDetails(), TransactionStatusDetails.SUCCESFULL.name());
        Assert.assertEquals(paymentTransactionEntryModel.getPaymentTransaction(), paymentTransactionModel);
    }

    @Test
    public void getAcceptedTransaction() {
        exceptionRule.expectMessage("order is not found with the given id");
        defaultAffirmPaymentTransactionStrategy.getAcceptedTransaction(null, PaymentTransactionType.AUTHORIZATION);
        exceptionRule.expectMessage("no payment information found");
        defaultAffirmPaymentTransactionStrategy.getAcceptedTransaction(cart, PaymentTransactionType.AUTHORIZATION);
        when(defaultAffirmPaymentTransactionStrategy.getTransaction(cart)).thenReturn(paymentTransactionModel);
        when(cart.getPaymentMode()).thenReturn(paymentModeModel);
        exceptionRule.expectMessage("no authorisation transaction found");
        defaultAffirmPaymentTransactionStrategy.getAcceptedTransaction(cart, PaymentTransactionType.AUTHORIZATION);
    }

    @Test
    public void getAcceptedTransactionSlient() {
        Assert.assertNull(defaultAffirmPaymentTransactionStrategy.getAcceptedTransactionSlient(null, PaymentTransactionType.AUTHORIZATION));
    }

    @Test
    public void isTransactionSuccessfull() {
        when(paymentTransactionEntryModel.getTransactionStatus()).thenReturn(TransactionStatus.REJECTED.name());
        Assert.assertFalse(defaultAffirmPaymentTransactionStrategy.isTransactionSuccessfull(paymentTransactionEntryModel));
    }
    
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();
}