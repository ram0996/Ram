package com.affirm.payment.service.executor.strategy;

import java.util.Map;

public interface AffirmPaymentEndpointStrategy {
   String getUrl(String endpointUrl, Map<String, Object> params);
}
