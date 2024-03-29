package com.affirm.payment.controllers.pages;

import de.hybris.platform.acceleratorservices.controllers.page.PageType;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractCheckoutController;
import de.hybris.platform.acceleratorstorefrontcommons.forms.GuestRegisterForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.commercefacades.coupon.data.CouponData;
import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.util.ResponsiveUtils;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.affirm.checkout.pojo.AffirmOrderTrackingResult;
import com.affirm.payment.controllers.AffirmpaymentaddonControllerConstants;
import com.affirm.payment.facade.AffirmPaymentFacade;

@Controller
@RequestMapping(value = "/checkout")
public class AffirmCheckoutController extends AbstractCheckoutController
{

   private static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";

   public static final String AFFIRM_ORDER_TRACKING = "affirmOrderTracking";

   private static final Logger LOG = Logger.getLogger(AffirmCheckoutController.class);

   private static final String ORDER_DATA = "orderData";
   private static final String AFFIRM_ORDER_TRACKING1 = "affirmOrderTracking";
   private static final String CHECKOUT_ORDER_CONFIRMATION_CMS_PAGE_LABEL = "orderConfirmation";
   private static final String CONTINUE_URL_KEY = "continueUrl";

   @Resource
   private Converter<OrderData, AffirmOrderTrackingResult> affirmTrackingOrderConverter;
   @Resource
   private AffirmPaymentFacade affirmPaymentFacade;
   @Resource
   private ProductFacade productFacade;
   @Resource
   private OrderFacade orderFacade;
   @Resource(name = "checkoutFacade")
   private CheckoutFacade checkoutFacade;

   @RequestMapping(method =RequestMethod.GET)
   public String checkout(final RedirectAttributes redirectModel)
   {
      if (getCheckoutFlowFacade().hasValidCart())
      {
         if (validateCart(redirectModel))
         {
            return REDIRECT_PREFIX + "/cart";
         }
         else
         {
            checkoutFacade.prepareCartForCheckout();
            return getCheckoutRedirectUrl();
         }
      }

      LOG.info("Missing, empty or unsupported cart");

      // No session cart or empty session cart. Bounce back to the cart page.
      return REDIRECT_PREFIX + "/cart";
   }

   @RequestMapping(value = "/orderConfirmation/" + ORDER_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
   @RequireHardLogIn
   public String orderConfirmation(@PathVariable("orderCode") final String orderCode, final HttpServletRequest request,
           final Model model, final RedirectAttributes redirectModel) throws CMSItemNotFoundException
   {

      String result = processOrderCode(orderCode, model, request, redirectModel);

      OrderData orderDetails = (OrderData) model.asMap().get(ORDER_DATA);
      if (orderDetails != null && affirmPaymentFacade.isAffirmOrder(orderDetails) && getCmsSiteService().getCurrentSite().getAffirmConfigContainer().isAnalyticsEnabled())
      {
         model.addAttribute(AFFIRM_ORDER_TRACKING1, Boolean.TRUE);
      }
      return result;
   }

   @RequestMapping(value = "/orderConfirmation/tracking/" + ORDER_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
   @RequireHardLogIn
   @ResponseBody
   public AffirmOrderTrackingResult orderConfirmation(@PathVariable("orderCode") final String orderCode) throws CMSItemNotFoundException
   {
      final OrderData orderDetails;

      try
      {
         orderDetails = orderFacade.getOrderDetailsForCode(orderCode);
      }
      catch (final UnknownIdentifierException e)
      {
         LOG.warn("Attempted to load an order confirmation that does not exist or is not visible. Redirect to home page.");
         return null;
      }

      if (orderDetails != null && affirmPaymentFacade.isAffirmOrder(orderDetails) && getCmsSiteService().getCurrentSite().getAffirmConfigContainer().isAnalyticsEnabled())
      {

         if (orderDetails.isGuestCustomer() && !StringUtils.substringBefore(orderDetails.getUser().getUid(), "|")
                 .equals(getSessionService().getAttribute(WebConstants.ANONYMOUS_CHECKOUT_GUID)))
         {
            return null;
         }

         if (orderDetails.getEntries() != null && !orderDetails.getEntries().isEmpty())
         {
            for (final OrderEntryData entry : orderDetails.getEntries())
            {
               final String productCode = entry.getProduct().getCode();
               final ProductData product = productFacade.getProductForCodeAndOptions(productCode,
                       Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.CATEGORIES, ProductOption.CLASSIFICATION));
               entry.setProduct(product);
            }
         }

         return affirmTrackingOrderConverter.convert(orderDetails);
      }
      return null;
   }

   protected String processOrderCode(final String orderCode, final Model model, final HttpServletRequest request,
           final RedirectAttributes redirectModel) throws CMSItemNotFoundException
   {
      final OrderData orderDetails;

      try
      {
         orderDetails = orderFacade.getOrderDetailsForCode(orderCode);
      }
      catch (final UnknownIdentifierException e)
      {
         LOG.warn("Attempted to load an order confirmation that does not exist or is not visible. Redirect to home page.");
         return REDIRECT_PREFIX + ROOT;
      }

      addRegistrationConsentDataToModel(model);

      if (orderDetails.isGuestCustomer() && !StringUtils.substringBefore(orderDetails.getUser().getUid(), "|")
              .equals(getSessionService().getAttribute(WebConstants.ANONYMOUS_CHECKOUT_GUID)))
      {
         return getCheckoutRedirectUrl();
      }

      if (orderDetails.getEntries() != null && !orderDetails.getEntries().isEmpty())
      {
         for (final OrderEntryData entry : orderDetails.getEntries())
         {
            final String productCode = entry.getProduct().getCode();
            final ProductData product = productFacade.getProductForCodeAndOptions(productCode,
                    Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.CATEGORIES));
            entry.setProduct(product);
         }
      }

      model.addAttribute("orderCode", orderCode);
      model.addAttribute("orderData", orderDetails);
      model.addAttribute("allItems", orderDetails.getEntries());
      model.addAttribute("deliveryAddress", orderDetails.getDeliveryAddress());
      model.addAttribute("deliveryMode", orderDetails.getDeliveryMode());
      model.addAttribute("paymentInfo", orderDetails.getPaymentInfo());
      model.addAttribute("pageType", PageType.ORDERCONFIRMATION.name());

      final List<CouponData> giftCoupons = orderDetails.getAppliedOrderPromotions().stream()
              .filter(x -> CollectionUtils.isNotEmpty(x.getGiveAwayCouponCodes())).flatMap(p -> p.getGiveAwayCouponCodes().stream())
              .collect(Collectors.toList());
      model.addAttribute("giftCoupons", giftCoupons);

      processEmailAddress(model, orderDetails);

      final String continueUrl = (String) getSessionService().getAttribute(WebConstants.CONTINUE_URL);
      model.addAttribute(CONTINUE_URL_KEY, (continueUrl != null && !continueUrl.isEmpty()) ? continueUrl : ROOT);

      final AbstractPageModel cmsPage = getContentPageForLabelOrId(CHECKOUT_ORDER_CONFIRMATION_CMS_PAGE_LABEL);
      storeCmsPageInModel(model, cmsPage);
      setUpMetaDataForContentPage(model, getContentPageForLabelOrId(CHECKOUT_ORDER_CONFIRMATION_CMS_PAGE_LABEL));
      model.addAttribute(ThirdPartyConstants.SeoRobots.META_ROBOTS, ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW);

      if (ResponsiveUtils.isResponsive())
      {
         return getViewForPage(model);
      }

      return AffirmpaymentaddonControllerConstants.CheckoutConfirmationPage;
   }

   protected void processEmailAddress(final Model model, final OrderData orderDetails)
   {
      final String uid;

      if (orderDetails.isGuestCustomer() && !model.containsAttribute("guestRegisterForm"))
      {
         final GuestRegisterForm guestRegisterForm = new GuestRegisterForm();
         guestRegisterForm.setOrderCode(orderDetails.getGuid());
         uid = orderDetails.getPaymentInfo().getBillingAddress().getEmail();
         guestRegisterForm.setUid(uid);
         model.addAttribute(guestRegisterForm);
      }
      else
      {
         uid = orderDetails.getUser().getUid();
      }
      model.addAttribute("email", uid);
   }


}
