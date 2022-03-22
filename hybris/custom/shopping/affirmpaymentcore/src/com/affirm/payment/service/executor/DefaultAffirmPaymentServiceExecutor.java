package com.affirm.payment.service.executor;

import com.affirm.payment.service.executor.provider.PaymentServiceProvider;
import com.affirm.payment.service.executor.request.AffirmPaymentServiceRequest;
import com.affirm.payment.service.executor.result.AffirmPaymentServiceResult;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import java.util.Set;

public class DefaultAffirmPaymentServiceExecutor implements AffirmPaymentServiceExecutor{

   private Set<PaymentServiceProvider> providers;

   @Override
   public AffirmPaymentServiceResult execute(final AffirmPaymentServiceRequest request)
   {
      Assert.notNull(request, "payment service request cannot be null");
      if(request == null){
         throw new IllegalArgumentException("request cannot be null");
      }

      final PaymentServiceProvider serviceProvider = providers.stream()
            .filter(ps -> ps.supports(request))
            .findAny()
            .orElseThrow(
                  () -> new UnsupportedOperationException("no payment service provider found for request: " + request)
            );

      if(!serviceProvider.valid(request)){
         throw new IllegalArgumentException("request cannot be null");
      }

      return serviceProvider.execute(request);
   }

   @Required
   public void setProviders(Set<PaymentServiceProvider> providers) {
      this.providers = providers;
   }
}
