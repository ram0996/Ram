package com.affirm.payment.controllers.pages;

import com.affirm.checkout.pojo.AffirmCheckoutData;
import com.affirm.payment.controllers.form.AffirmAddressForm;
import com.affirm.payment.controllers.form.validator.AffirmAddressFormValidator;
import com.affirm.payment.exception.AffirmPaymentException;
import com.affirm.payment.facade.AffirmPaymentFacade;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

//@Controller
@RequestMapping("/checkout/affirm")
public class AffirmPaymentController extends PaymentMethodCheckoutStepController {

   private static final Logger LOG = LoggerFactory.getLogger(AffirmPaymentController.class);

   public static final String CHECKOUT_AFFIRM_ERROR = "/checkout/multi/payment-method/add";
   private static final String PAYMENT_METHOD = "payment-method";

   @Autowired private HttpServletRequest request;

   @Resource(name = "productVariantFacade") private ProductFacade productFacade;
   @Resource private CartService cartService;
   @Resource private AffirmPaymentFacade affirmPaymentFacade;
   @Resource private Converter<CartModel, AffirmCheckoutData> affirmRequestConverter;
   @Resource private Converter<AffirmAddressForm, AddressData> affirmAddressFormConverter;
   @Resource private AffirmAddressFormValidator affirmAddressFormValidator;

   @ResponseBody
   @RequestMapping(value = "/affirmCheckout", method = {RequestMethod.POST})
   public AffirmCheckoutData returnCheckoutData(final Model model) {
      AffirmCheckoutData affirmCheckoutData = null;
      if(cartService.hasSessionCart() && affirmPaymentFacade.isAffirmPaymentEnabled()) {
         try {
            affirmCheckoutData = affirmRequestConverter.convert(cartService.getSessionCart());
         }catch (RuntimeException re){
            LOG.warn("Error during create Affirm checkout data", re);
         }
      }
      return affirmCheckoutData;
   }

   @RequestMapping(value = "/cancel", method = {RequestMethod.POST,  RequestMethod.GET})
   public String cancel(final Model model, final RedirectAttributes redirectAttributes) {
      String message = getMessageSource().getMessage("text.affirm.cancel", null, getI18nService().getCurrentLocale());
      redirectAttributes.addFlashAttribute("errorMsg", message);
      return REDIRECT_PREFIX + CHECKOUT_AFFIRM_ERROR;
   }

   @RequestMapping(value = "/billing", method = {RequestMethod.POST})
   public String saveBillingAddress(final HttpServletRequest request,
         final AffirmAddressForm affirmAddressForm,
         final BindingResult bindingResult, final Model model,
         final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {

      final Map<String, String> resultMap = getRequestParameterMap(request);

      affirmAddressFormValidator.validate(affirmAddressForm, bindingResult);
      //save billing address

      if(bindingResult.hasErrors()){
         GlobalMessages.addErrorMessage(model, "checkout.multi.paymentMethod.affirm.addressError");
         String result = enterStep(model, redirectAttributes);
         model.addAttribute("affirmAddressForm", affirmAddressForm);
         return result;
      }

      affirmPaymentFacade.initizePayment(affirmAddressFormConverter.convert(affirmAddressForm));

      return getCheckoutStep().nextStep();
   }

   @RequestMapping(value =  "/authorise", method = {RequestMethod.POST,RequestMethod.GET})
   public String authorise(@RequestParam(required=false) final String checkout_token,
         final Model model, final RedirectAttributes redirectModel) {

      boolean success = false;

      try
      {
         success = affirmPaymentFacade.authorisePayment(checkout_token);
      }
      catch (RuntimeException re)
      {
         LOG.warn("error during payment authorisation ", re);
      }
      if(success){
         final OrderData orderData;
         try
         {
            orderData = getCheckoutFacade().placeOrder();
         }
         catch (final Exception e)
         {
            LOG.error("Failed to place Order", e);
            //TODO-BE auth reversal
            //GlobalMessages.addErrorMessage(model, "checkout.affirm.order.failed");
            return REDIRECT_PREFIX + CHECKOUT_AFFIRM_ERROR;
         }

         return redirectToOrderConfirmationPage(orderData);
      }else {
         //GlobalMessages.addErrorMessage(redirectModel,"checkout.affirm.authorisation.failed");
         return REDIRECT_PREFIX + CHECKOUT_AFFIRM_ERROR;
      }
   }

   @ResponseBody
   @RequestMapping(value =  "/refund", method = {RequestMethod.POST,RequestMethod.GET})
   public String refundOrder(@RequestParam("order_id") final String orderId,
         @RequestParam("refund_amount") final Double refundAmount,
         final Model model, final RedirectAttributes redirectModel) {

      if(!affirmPaymentFacade.isAffirmPaymentEnabled()){
         return "NOT_SUPPORTED";
      }
      BigDecimal refund = null;
      if(refundAmount != null){
         refund = BigDecimal.valueOf(refundAmount);
      }

      try {
         boolean success = affirmPaymentFacade.refundOrder(orderId, refund);
         if(success) {
            return "ok";
         }else {
            return "nok";
         }
      }catch (AffirmPaymentException ap){
         return ap.getReasonCode() + " - " + ap.getMessage();
      }

   }

   @ResponseBody
   @RequestMapping(value =  "/capture", method = {RequestMethod.POST,RequestMethod.GET})
   public String captureOrder(@RequestParam("order_id") final String orderId,
         final Model model, final RedirectAttributes redirectModel) {

      if(!affirmPaymentFacade.isAffirmPaymentEnabled()){
         return "NOT_SUPPORTED";
      }

      try {
         boolean success = affirmPaymentFacade.capturePayment(orderId);
         if(success) {
            return "ok";
         }else {
            return "nok";
         }
      }catch (AffirmPaymentException ap){
         return ap.getReasonCode() + " - " + ap.getMessage();
      }
   }

   @ResponseBody
   @RequestMapping(value =  "/void", method = {RequestMethod.POST,RequestMethod.GET})
   public String voidOrder(@RequestParam("order_id") final String orderId,
         final Model model, final RedirectAttributes redirectModel) {

      if(!affirmPaymentFacade.isAffirmPaymentEnabled()){
         return "NOT_SUPPORTED";
      }

      try {
         boolean success = affirmPaymentFacade.voidPayment(orderId);
         if(success) {
            return "ok";
         }else {
            return "nok";
         }
      }catch (AffirmPaymentException ap){
         return ap.getReasonCode() + " - " + ap.getMessage();
      }
   }

   private void setupPage(Model model) {
      final CartData cartData = getCheckoutFacade().getCheckoutCart();
      model.addAttribute("cartData", cartData);

   }


   @Override protected String getSilentOrderPostPage() {
      //return PAYMENT_PAGE;
      return "addon:/affirmpaymentaddon/checkout/silentOrderPostPage";
   }




   protected CheckoutStep getCheckoutStep()
   {
      return getCheckoutStep(PAYMENT_METHOD);
   }
}
