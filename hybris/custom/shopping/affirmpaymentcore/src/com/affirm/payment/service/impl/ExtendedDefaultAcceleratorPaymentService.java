package com.affirm.payment.service.impl;

import de.hybris.platform.acceleratorservices.model.payment.CCPaySubValidationModel;
import de.hybris.platform.acceleratorservices.payment.data.CreateSubscriptionResult;
import de.hybris.platform.acceleratorservices.payment.data.PaymentErrorField;
import de.hybris.platform.acceleratorservices.payment.data.PaymentSubscriptionResultItem;
import de.hybris.platform.acceleratorservices.payment.enums.DecisionsEnum;
import de.hybris.platform.acceleratorservices.payment.impl.DefaultAcceleratorPaymentService;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.CustomerModel;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

public class ExtendedDefaultAcceleratorPaymentService extends DefaultAcceleratorPaymentService {

   private static final Logger LOG = Logger.getLogger(ExtendedDefaultAcceleratorPaymentService.class);
   private static final String SUBSCRIPTION_SIGNATURE_DATA_CANNOT_BE_NULL_MSG = "SubscriptionSignatureData cannot be null";
   private static final String SUBSCRIPTION_INFO_DATA_CANNOT_BE_NULL_MSG = "SubscriptionInfoData cannot be null";
   private static final String SIGNATURE_DATA_CANNOT_BE_NULL_MSG = "SignatureData cannot be null";
   private static final String PAYMENT_INFO_DATA_CANNOT_BE_NULL_MSG = "PaymentInfoData cannot be null";
   private static final String ORDER_INFO_DATA_CANNOT_BE_NULL_MSG = "OrderInfoData cannot be null";
   private static final String CUSTOMER_INFO_DATA_CANNOT_BE_NULL_MSG = "CustomerInfoData cannot be null";
   private static final String AUTH_REPLY_DATA_CANNOT_BE_NULL_MSG = "AuthReplyData cannot be null";
   private static final String DECISION_CANNOT_BE_NULL_MSG = "Decision cannot be null";
   private static final String CREATE_SUBSCRIPTION_RESULT_CANNOT_BE_NULL_MSG = "CreateSubscriptionResult cannot be null";

   public static final String AFFIRM_ID = "affirm_id";

   @Override public PaymentSubscriptionResultItem completeSopCreatePaymentSubscription(CustomerModel customerModel, boolean saveInAccount,
         Map<String, String> parameters) {
      final PaymentSubscriptionResultItem paymentSubscriptionResult = new PaymentSubscriptionResultItem();
      final Map<String, PaymentErrorField> errors = new HashMap<String, PaymentErrorField>();
      paymentSubscriptionResult.setErrors(errors);

      final CreateSubscriptionResult response = getPaymentResponseInterpretation().interpretResponse(parameters,
            getClientReferenceLookupStrategy().lookupClientReferenceId(), errors);

      validateParameterNotNull(response, CREATE_SUBSCRIPTION_RESULT_CANNOT_BE_NULL_MSG);
      validateParameterNotNull(response.getDecision(), DECISION_CANNOT_BE_NULL_MSG);

      if (!getCreateSubscriptionResultValidationStrategy().validateCreateSubscriptionResult(errors, response).isEmpty())
      {
         return paymentSubscriptionResult;
      }

      paymentSubscriptionResult.setSuccess(DecisionsEnum.ACCEPT.name().equalsIgnoreCase(response.getDecision()));
      paymentSubscriptionResult.setDecision(String.valueOf(response.getDecision()));
      paymentSubscriptionResult.setResultCode(String.valueOf(response.getReasonCode()));

      if (DecisionsEnum.ACCEPT.name().equalsIgnoreCase(response.getDecision()))
      {
         // in case of ACCEPT we should have all these fields filled out
         Assert.notNull(response.getAuthReplyData(), AUTH_REPLY_DATA_CANNOT_BE_NULL_MSG);
         Assert.notNull(response.getCustomerInfoData(), CUSTOMER_INFO_DATA_CANNOT_BE_NULL_MSG);
         Assert.notNull(response.getOrderInfoData(), ORDER_INFO_DATA_CANNOT_BE_NULL_MSG);
         Assert.notNull(response.getPaymentInfoData(), PAYMENT_INFO_DATA_CANNOT_BE_NULL_MSG);
         Assert.notNull(response.getSignatureData(), SIGNATURE_DATA_CANNOT_BE_NULL_MSG);
         Assert.notNull(response.getSubscriptionInfoData(), SUBSCRIPTION_INFO_DATA_CANNOT_BE_NULL_MSG);
         Assert.notNull(response.getSubscriptionSignatureData(), SUBSCRIPTION_SIGNATURE_DATA_CANNOT_BE_NULL_MSG);

         // Validate signature
         if (getSignatureValidationStrategy().validateSignature(response.getSubscriptionInfoData()))
         {
            getPaymentTransactionStrategy().savePaymentTransactionEntry(customerModel, response.getRequestId(),
                  response.getOrderInfoData());
            final CreditCardPaymentInfoModel cardPaymentInfoModel = getCreditCardPaymentInfoCreateStrategy().saveSubscription(
                  customerModel, response.getCustomerInfoData(), response.getSubscriptionInfoData(),
                  response.getPaymentInfoData(), saveInAccount);
            paymentSubscriptionResult.setStoredCard(cardPaymentInfoModel);

            if(parameters.containsKey(AFFIRM_ID)){
               cardPaymentInfoModel.setAffirmVCNId(parameters.get(AFFIRM_ID));
               getModelService().save(cardPaymentInfoModel);
            }
            // Check if the subscription has already been validated
            final CCPaySubValidationModel subscriptionValidation = getCreditCardPaymentSubscriptionDao()
                  .findSubscriptionValidationBySubscription(cardPaymentInfoModel.getSubscriptionId());
            if (subscriptionValidation != null)
            {
               cardPaymentInfoModel.setSubscriptionValidated(true);
               getModelService().save(cardPaymentInfoModel);
               getModelService().remove(subscriptionValidation);
            }

         }
         else
         {
            LOG.error("Cannot create subscription. Subscription signature does not match.");
         }
      }
      else
      {
         LOG.error("Cannot create subscription. Decision: " + response.getDecision() + " - Reason Code: "
               + response.getReasonCode());
      }
      return paymentSubscriptionResult;
   }
}
