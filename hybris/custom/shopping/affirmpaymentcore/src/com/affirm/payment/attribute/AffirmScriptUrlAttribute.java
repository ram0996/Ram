package com.affirm.payment.attribute;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.affirm.payment.model.AffirmConfigContainerModel;

public class AffirmScriptUrlAttribute extends AbstractDynamicAttributeHandler<String, AffirmConfigContainerModel> {

   private String sandboxScriptUrl;
   private String liveScriptUrl;

   @Override public String get(AffirmConfigContainerModel configContainer) {
      if(configContainer.isSandboxMode()){
         return sandboxScriptUrl;
      }else{
         return liveScriptUrl;
      }
   }

   @Override public void set(AffirmConfigContainerModel configContainer, String value) {
      //do nothing
   }

   public String getSandboxScriptUrl()
   {
      return sandboxScriptUrl;
   }

   public void setSandboxScriptUrl(final String sandboxScriptUrl)
   {
      this.sandboxScriptUrl = sandboxScriptUrl;
   }

   public String getLiveScriptUrl()
   {
      return liveScriptUrl;
   }

   public void setLiveScriptUrl(final String liveScriptUrl)
   {
      this.liveScriptUrl = liveScriptUrl;
   }
}