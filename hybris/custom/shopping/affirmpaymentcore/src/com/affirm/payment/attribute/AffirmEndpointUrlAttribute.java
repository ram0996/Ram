package com.affirm.payment.attribute;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

import com.affirm.payment.model.AffirmConfigContainerModel;

public class AffirmEndpointUrlAttribute extends AbstractDynamicAttributeHandler<String, AffirmConfigContainerModel> {

   private String sandboxEndpointUrl;
   private String liveEndpointUrl;

   @Override public String get(AffirmConfigContainerModel configContainer) {
      if(configContainer.isSandboxMode()){
         return sandboxEndpointUrl;
      }else{
         return liveEndpointUrl;
      }
   }

   @Override public void set(AffirmConfigContainerModel configContainer, String value) {
      //do nothing
   }

   public String getSandboxEndpointUrl()
   {
      return sandboxEndpointUrl;
   }

   public void setSandboxEndpointUrl(final String sandboxEndpointUrl)
   {
      this.sandboxEndpointUrl = sandboxEndpointUrl;
   }

   public String getLiveEndpointUrl()
   {
      return liveEndpointUrl;
   }

   public void setLiveEndpointUrl(final String liveEndpointUrl)
   {
      this.liveEndpointUrl = liveEndpointUrl;
   }
}