package com.affirm.payment.service.executor.strategy;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

public class DefaultAffirmPaymentEndpointStrategy implements AffirmPaymentEndpointStrategy {

   private static final Logger LOG = LoggerFactory.getLogger(DefaultAffirmPaymentEndpointStrategy.class);

   public static final String BASE_URL = "BASE_URL";

   private String urlPattern;
   private Map<String, String> mapping;

   @Override public String getUrl(String endpoint, Map<String, Object> params) {
      String finalUrl = urlPattern;
      if(MapUtils.isNotEmpty(params)){
         for (String key: mapping.keySet()) {
            Object value = params.get(mapping.get(key));
            if(finalUrl.contains(key) && value != null){
               finalUrl = finalUrl.replace(key, value.toString());
            }
         }
      }

      finalUrl = finalUrl.replace(BASE_URL, endpoint);

      LOG.debug(String.format("url pattern: %s, final url %s", urlPattern, finalUrl));
      return finalUrl;
   }

   public String getUrlPattern() {
      return urlPattern;
   }

   @Required
   public void setUrlPattern(String urlPattern) {
      this.urlPattern = urlPattern;
   }

   public Map<String, String> getMapping() {
      return mapping;
   }

   public void setMapping(Map<String, String> mapping) {
      this.mapping = mapping;
   }
}
