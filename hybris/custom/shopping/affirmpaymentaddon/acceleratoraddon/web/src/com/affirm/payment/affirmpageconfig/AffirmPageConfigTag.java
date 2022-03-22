package com.affirm.payment.affirmpageconfig;

import com.affirm.payment.core.enums.AffirmPromoPageType;
import com.affirm.payment.model.AffirmConfigContainerModel;
import com.affirm.payment.model.AffirmPageConfigModel;
import com.affirm.payment.model.AffirmPromoMessageModel;
import de.hybris.platform.acceleratorstorefrontcommons.tags.Functions;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;

public class AffirmPageConfigTag extends BodyTagSupport implements DynamicAttributes {

    protected Map<String, String> dynamicAttributes; // NOSONAR
    private static final Logger LOG = Logger.getLogger(AffirmPageConfigTag.class);
    private String price;
    private String category;
    private String brand;
    private String promoId;
    private String sku;
    private String pageType;

    private String getAffirmLink() {
        HttpServletRequest httpServletRequest = (HttpServletRequest)this.pageContext.getRequest();
        CMSSiteService cmsSiteService = Functions.getSpringBean(httpServletRequest,"cmsSiteService",CMSSiteService.class);
        CMSSiteModel cmsSiteModel = cmsSiteService.getCurrentSite();
        AffirmConfigContainerModel affirmConfigContainer = cmsSiteModel.getAffirmConfigContainer();

        if(affirmConfigContainer == null || !affirmConfigContainer.isEnabled() || StringUtils.isEmpty(affirmConfigContainer.getAffirmPublicKey())){
            return null;
        }


        Collection<AffirmPageConfigModel> affirmPageConfigModelCollection = affirmConfigContainer.getAffirmPageConfig();
        for(AffirmPageConfigModel affirmPageConfigModel: affirmPageConfigModelCollection){
            if(affirmPageConfigModel.getShowAffirmPromotionFlag()) {
                for (AffirmPromoMessageModel affirmPromoMessageModel : affirmPageConfigModel.getAffirmPromoMessageList()) {
                    if (getAffirmPageType() !=null && getAffirmPageType().equals(affirmPromoMessageModel.getAffirmPageType())){
                        if(BooleanUtils.isTrue(affirmPageConfigModel.getShowAffirmPromotionFlag()))
                        {
                            return affirmPromoMessageModel.getAffirmPromotionMessage();
                        }else {
                            return null;
                        }

                    }
                }
            }
        }
        return null;


    }

    private AffirmPromoPageType getAffirmPageType(){
        if(pageType.equalsIgnoreCase(AffirmPromoPageType.PDP.getCode())){
            return AffirmPromoPageType.PDP;
        }
        else if (pageType.equalsIgnoreCase(AffirmPromoPageType.CATEGORY.getCode())){
            return AffirmPromoPageType.CATEGORY;
        }
        else if(pageType.equalsIgnoreCase(AffirmPromoPageType.CART.getCode())){
            return AffirmPromoPageType.CART;
        }

        return null;
    }

    public int doStartTag() {

        StringBuilder bodyTagBuilder = new StringBuilder();
        double priceValue = 0.0D;
        if(StringUtils.isNotBlank(getPrice())) {
            priceValue = Double.parseDouble(getPrice()) * 100;
        }
        String affirmLink = getAffirmLink();
        if(StringUtils.isEmpty(affirmLink)){
            return 0;
        }
        String promMessageLink = String.format(affirmLink, (long)priceValue, getCategory(), getBrand(), getSku(), getPromoId());

        promMessageLink = promMessageLink.replaceAll("data-promo-id=\"null\"", "");
        promMessageLink = promMessageLink.replaceAll("data-category=\"null\"", "");
        promMessageLink = promMessageLink.replaceAll("data-sku=\"null\"", "");

        bodyTagBuilder.append(promMessageLink);
        try {
            this.pageContext.getOut().print(bodyTagBuilder.toString() +"\n");
        } catch (IOException exception) {
            LOG.warn("Error processing tag", exception);
        }

        return 0;
    }

    @Override
    public void setDynamicAttribute(String uri, String attrName, Object attrValue) {
        if(attrValue!=null) {
            dynamicAttributes.put(attrName, attrValue.toString());
        }
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getPromoId() {
        return promoId;
    }

    public void setPromoId(String promoId) {
        this.promoId = promoId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }
}
