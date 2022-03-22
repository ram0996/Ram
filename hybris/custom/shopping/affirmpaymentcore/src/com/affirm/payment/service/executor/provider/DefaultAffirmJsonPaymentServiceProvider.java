package com.affirm.payment.service.executor.provider;

import com.affirm.payment.AffirmPaymentConstants;
import com.affirm.payment.core.client.AffirmHTTPClient;
import com.affirm.payment.core.service.AffirmPaymentTransactionStrategy;
import com.affirm.payment.model.AffirmConfigContainerModel;
import com.affirm.payment.model.AffirmPaymentTransactionEntryModel;
import com.affirm.payment.service.executor.request.AffirmPaymentServiceRequest;
import com.affirm.payment.service.executor.request.validator.PaymentRequestValidator;
import com.affirm.payment.service.executor.response.AffirmPaymentResponseStrategy;
import com.affirm.payment.service.executor.result.AffirmPaymentServiceResult;
import com.affirm.payment.service.executor.strategy.AffirmPaymentEndpointStrategy;
import com.google.gson.*;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class DefaultAffirmJsonPaymentServiceProvider implements PaymentServiceProvider {

   private static final Logger LOG = Logger.getLogger(DefaultAffirmJsonPaymentServiceProvider.class);

   private AffirmPaymentEndpointStrategy affirmPaymentEndpointStrategy;
   private AffirmPaymentResponseStrategy affirmPaymentResponseStrategy;
   private PaymentTransactionType paymentTransactionType;
   private PaymentRequestValidator paymentRequestValidator;
   private Converter<AffirmPaymentServiceRequest, HashMap<String,String>> requestConverter;

   @Resource private AffirmHTTPClient affirmHTTPClient;
   @Resource private AffirmPaymentTransactionStrategy affirmPaymentTransactionStrategy;
   @Resource private ModelService modelService;

   @Override public boolean supports(AffirmPaymentServiceRequest request) {
      return Objects.equals(paymentTransactionType, request.getPaymentTransactionType());
   }

   @Override public AffirmPaymentServiceResult execute(AffirmPaymentServiceRequest request) {
      AbstractOrderModel cart = (AbstractOrderModel) request.getParams().get(AffirmPaymentConstants.ORDER);
      AffirmConfigContainerModel affirmConfiguration = cart.getSite().getAffirmConfigContainer();
      String requestId = (String) request.getParams().get(AffirmPaymentConstants.Authorisation.CHECKOUT_TOKEN);


      PaymentTransactionModel transaction = affirmPaymentTransactionStrategy.getOrCreateTransaction(cart);
      Map requestMap = requestConverter.convert(request);
      String endpointUrl = affirmPaymentEndpointStrategy.getUrl(affirmConfiguration.getEndpoint(), request.getParams());

      String jsonRequest = new Gson().toJson(requestMap);
      if (LOG.isDebugEnabled()){
         LOG.debug(String.format("send '%s' json request to %s endpoint ", jsonRequest, endpointUrl));
      }

      String result = affirmHTTPClient.send(endpointUrl, jsonRequest, affirmConfiguration);

      if (LOG.isDebugEnabled()){
         LOG.debug(String.format("result: '%s'", result));
      }

      AffirmPaymentTransactionEntryModel authorisationEntry = processResultJson(cart, requestId, transaction, result);

      affirmPaymentResponseStrategy.validateTransactionResult(cart, authorisationEntry);
      modelService.save(authorisationEntry);
      return AffirmPaymentServiceResult.create(authorisationEntry);
   }

   public AffirmPaymentTransactionEntryModel processResultJson(AbstractOrderModel order, String requestId, PaymentTransactionModel transaction,
         String jsonResponse) {
      AffirmPaymentTransactionEntryModel transactionEntry = affirmPaymentTransactionStrategy.createTransactionEntryModel(
            AffirmPaymentTransactionEntryModel.class, requestId, paymentTransactionType, transaction);
      transactionEntry.setAuthorisationJSON(jsonResponse);
      transactionEntry.setTransactionStatus(TransactionStatus.ERROR.name());
      if(jsonResponse == null){
         return transactionEntry;
      }

      HashMap<String, String> properties = new HashMap<>();
      JsonElement jsonElement = new JsonParser().parse(jsonResponse);


      JsonObject jsonObject = jsonElement.getAsJsonObject();


      Iterator<Map.Entry<String, JsonElement>> it = jsonObject.entrySet().iterator();
      while (it.hasNext()){
         Map.Entry<String, JsonElement> entry = it.next();
         if(entry.getValue() == null || entry.getValue() instanceof JsonNull){
            continue;
         }
         if(entry.getValue() instanceof JsonObject || entry.getValue() instanceof JsonArray) {
            properties.put(entry.getKey(), entry.getValue().toString());
         }else {
            properties.put(entry.getKey(), entry.getValue().getAsString());
         }
      }

      transactionEntry.setProperties(properties);

      modelService.save(transactionEntry);
      return transactionEntry;
   }



   @Override public boolean valid(AffirmPaymentServiceRequest request) {
      return paymentRequestValidator == null || paymentRequestValidator.isValid(request);
   }

   public PaymentTransactionType getPaymentTransactionType() {
      return paymentTransactionType;
   }

   @Required
   public void setPaymentTransactionType(PaymentTransactionType paymentTransactionType) {
      this.paymentTransactionType = paymentTransactionType;
   }

   public PaymentRequestValidator getPaymentRequestValidator() {
      return paymentRequestValidator;
   }

   public void setPaymentRequestValidator(PaymentRequestValidator paymentRequestValidator) {
      this.paymentRequestValidator = paymentRequestValidator;
   }

   @Required
   public void setRequestConverter(Converter<AffirmPaymentServiceRequest, HashMap<String, String>> requestConverter) {
      this.requestConverter = requestConverter;
   }

   public void setAffirmPaymentEndpointStrategy(AffirmPaymentEndpointStrategy affirmPaymentEndpointStrategy) {
      this.affirmPaymentEndpointStrategy = affirmPaymentEndpointStrategy;
   }

   @Required
   public void setAffirmPaymentResponseStrategy(AffirmPaymentResponseStrategy affirmPaymentResponseStrategy) {
      this.affirmPaymentResponseStrategy = affirmPaymentResponseStrategy;
   }
}
