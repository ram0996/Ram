package com.affirm.payment.converter;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.url.impl.AbstractUrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.promotionengineservices.model.AbstractRuleBasedPromotionActionModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedOrderAdjustTotalActionModel;
import de.hybris.platform.promotionengineservices.model.RuleBasedOrderEntryAdjustActionModel;
import de.hybris.platform.promotions.model.AbstractPromotionActionModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.variants.model.VariantProductModel;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.affirm.checkout.pojo.AffirmAddressData;
import com.affirm.checkout.pojo.AffirmBillingData;
import com.affirm.checkout.pojo.AffirmCheckoutData;
import com.affirm.checkout.pojo.AffirmItemData;
import com.affirm.checkout.pojo.AffirmMerchantData;
import com.affirm.checkout.pojo.AffirmMetadata;
import com.affirm.checkout.pojo.AffirmNameData;
import com.affirm.checkout.pojo.AffirmShippingData;

public class AffirmCheckoutDataPopulator implements Populator<CartModel, AffirmCheckoutData> {

   public static final String CHECKOUT_AFFIRM_CANCEL_URL = "/checkout/affirm/cancel";
   public static final String CHECKOUT_AFFIRM_CONFIRMATION_URL = "/checkout/affirm/authorise";

   public static final String POST = "POST";
   private static final Logger LOG = Logger.getLogger(AffirmCheckoutDataPopulator.class);

   @Resource SiteConfigService siteConfigService;
   @Resource ConfigurationService configurationService;
   @Resource BaseSiteService baseSiteService;
   @Resource CustomerEmailResolutionService customerEmailResolutionService;
   @Resource AbstractUrlResolver<ProductModel> productModelUrlResolver;
   @Resource SiteBaseUrlResolutionService siteBaseUrlResolutionService;

   @Override public void populate(CartModel cartModel, AffirmCheckoutData checkoutData) throws ConversionException {
      if(cartModel == null){
         return;
      }

      String email = customerEmailResolutionService.getEmailForCustomer((CustomerModel) cartModel.getUser());


      AffirmShippingData shipping = new AffirmShippingData();
      checkoutData.setShipping(shipping);
      AffirmBillingData billing = new AffirmBillingData();
      checkoutData.setBilling(billing);

      populateAddress(email, cartModel.getDeliveryAddress(), checkoutData.getShipping());
      populateAddress(email, cartModel.getPaymentInfo().getBillingAddress(), checkoutData.getBilling());

      checkoutData.setMetadata(populateMetadata(cartModel));

      checkoutData.setOrder_id(cartModel.getCode());
      checkoutData.setShipping_amount(convertToBigInteger(cartModel.getDeliveryCost()));
      checkoutData.setTax_amount(convertToBigInteger(cartModel.getTotalTax()));

      checkoutData.setTotal(convertToBigInteger(cartModel.getTotalIncludingTax()));

      populateDiscounts(cartModel, checkoutData);
      populateItems(cartModel, checkoutData);

      checkoutData.setMerchant(populateMerchantData());

   }

   private void populateDiscounts(final CartModel cartModel, final AffirmCheckoutData checkoutData)
   {
      try
      {
         if (CollectionUtils.isNotEmpty(cartModel.getAllPromotionResults()))
         {
            HashMap<String, Map<String, Object>> discounts = new HashMap<>();


            for (PromotionResultModel promo : cartModel.getAllPromotionResults())
            {

               HashMap<String, Object> promoDetails = new HashMap<>();
               String messageFired = promo.getMessageFired();
               if (StringUtils.isEmpty(messageFired))
               {
                  messageFired = "nomessage";
               }
               String couponCode = getCouponCode(promo);
               if (couponCode == null)
               {
                  couponCode = messageFired;
               }
               promoDetails.put("discount_display_name", messageFired);

               Long value = getValue(cartModel, promo);
               promoDetails.put("discount_amount", value);
               discounts.put(couponCode, promoDetails);

            }
            checkoutData.setDiscounts(discounts);
         }
      }catch (Exception e){
         LOG.warn("unexpected error during discount calculation ", e);
      }

   }

   private Long getValue(final CartModel cartModel, final PromotionResultModel promo)
   {
      if(cartModel.getAllPromotionResults().size() == 1){
         return Double.valueOf(cartModel.getTotalDiscounts() * 100).longValue();
      }

      if(CollectionUtils.isNotEmpty(promo.getActions())){
         Double value = 0.0;
         for (AbstractPromotionActionModel action :promo.getActions())
         {
            if(action instanceof RuleBasedOrderAdjustTotalActionModel){
               value =+ ((RuleBasedOrderAdjustTotalActionModel)action).getAmount().doubleValue();
            }
            if(action instanceof RuleBasedOrderEntryAdjustActionModel){
               value =+ ((RuleBasedOrderEntryAdjustActionModel)action).getAmount().doubleValue();
            }
         }
         value *=100;
         return value.longValue();
      }
      return null;

   }

   private String getCouponCode(final PromotionResultModel promo)
   {
      if (CollectionUtils.isNotEmpty(promo.getActions()))
      {
         for (AbstractPromotionActionModel action : promo.getActions())
         {
            if (action instanceof AbstractRuleBasedPromotionActionModel)
            {
               AbstractRuleBasedPromotionActionModel promoAction = (AbstractRuleBasedPromotionActionModel) action;
               if(CollectionUtils.isNotEmpty(promoAction.getUsedCouponCodes()))
               {
                  return promoAction.getUsedCouponCodes().stream().collect(Collectors.joining(","));
               }
            }
         }

      }
      return promo.getPromotion().getCode();
   }


   private AffirmMerchantData populateMerchantData() {
      AffirmMerchantData merchant = new AffirmMerchantData();
      merchant.setName(siteConfigService.getProperty("affirm.merchant.name"));
      merchant.setUser_cancel_url(covertToAbsoluteURL(CHECKOUT_AFFIRM_CANCEL_URL, true));
      merchant.setUser_confirmation_url(covertToAbsoluteURL(CHECKOUT_AFFIRM_CONFIRMATION_URL, true));
      merchant.setUser_confirmation_url_action(POST);
      return merchant;
   }

   private void populateItems(CartModel cartModel, AffirmCheckoutData checkoutData) {
      if(CollectionUtils.isNotEmpty(cartModel.getEntries())){
         AffirmItemData[] items = new  AffirmItemData[cartModel.getEntries().size()];
         int index = 0;
         for (AbstractOrderEntryModel entry: cartModel.getEntries()) {
            AffirmItemData itemData = new AffirmItemData();
            itemData.setDisplay_name(entry.getProduct().getName());
            itemData.setQty(BigInteger.valueOf(entry.getQuantity()));
            itemData.setItem_url(productModelUrlResolver.resolve(entry.getProduct()));
            itemData.setSku(entry.getProduct().getCode());
            itemData.setUnit_price(convertToBigInteger(entry.getBasePrice()));
            if(entry.getProduct().getThumbnail() != null) {
               itemData.setItem_image_url(entry.getProduct().getThumbnail().getURL());
            }
            itemData.setCategories(getCategories(entry.getProduct()));
            items[index++] = itemData;
         }
         checkoutData.setItems(items);
      }
   }

   private String[] getCategories(final ProductModel product)
   {
      if(CollectionUtils.isNotEmpty(product.getSupercategories()))
      {
         return product.getSupercategories().stream().map(categoryModel -> categoryModel.getName())
                 .collect(Collectors.toList()).toArray(new String[product.getSupercategories().size()]);
      }

      if(product instanceof VariantProductModel){
         ProductModel baseProduct =  ((VariantProductModel) product).getBaseProduct();
         if (CollectionUtils.isNotEmpty(baseProduct.getSupercategories()))
         {
            return baseProduct.getSupercategories().stream().map(categoryModel -> categoryModel.getName())
                    .collect(Collectors.toList()).toArray(new String[baseProduct.getSupercategories().size()]);
         }
      }

      return null;
   }

   private BigInteger convertToBigInteger(Double doubleValue) {
      return BigInteger.valueOf(Double.valueOf(doubleValue * 100.0).longValue());
   }

   private AffirmMetadata populateMetadata(CartModel cartModel) {
      AffirmMetadata metadata = new AffirmMetadata();
      metadata.setPlatform_type(configurationService.getConfiguration().getString("affirm.platform.type","Hybris"));
      metadata.setPlatform_version(configurationService.getConfiguration().getString("affirm.platform.version","1.8.0.8"));
      metadata.setPlatform_affirm(configurationService.getConfiguration().getString("affirm.extension.version","Affirm_1.0"));
      metadata.setShipping_type(cartModel.getDeliveryMode().getName());
      if(cartModel.getSite().getAffirmConfigContainer().isModalMode()){
         metadata.setMode("modal");
      }
      return metadata;
   }

   private void populateAddress(String email, AddressModel deliveryAddress, AffirmShippingData shipping) {
      AffirmNameData nameData = new AffirmNameData();
      nameData.setFirst(deliveryAddress.getFirstname());
      nameData.setLast(deliveryAddress.getLastname());
      shipping.setName(nameData);

      populateAddress(deliveryAddress, shipping);

      shipping.setEmail(email);
      shipping.setPhone_number(deliveryAddress.getPhone1());

   }

   private void populateAddress(AddressModel deliveryAddress, AffirmShippingData shipping) {
      AffirmAddressData addressData = new AffirmAddressData();
      addressData.setCity(deliveryAddress.getTown());
      addressData.setLine1(deliveryAddress.getLine1());
      addressData.setLine2(deliveryAddress.getLine2());
      addressData.setCountry(deliveryAddress.getCountry().getIsocode());
      addressData.setZipcode(deliveryAddress.getPostalcode());
      if(deliveryAddress.getRegion() != null){
         addressData.setState(deliveryAddress.getRegion().getIsocodeShort());
      }
      shipping.setAddress(addressData);
   }

   private String covertToAbsoluteURL(final String relativeURL, final boolean secure)
   {
      return siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSiteService.getCurrentBaseSite(), secure, relativeURL);
   }

}
