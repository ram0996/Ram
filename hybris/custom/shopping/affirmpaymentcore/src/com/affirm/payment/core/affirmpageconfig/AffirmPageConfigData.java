package com.affirm.payment.core.affirmpageconfig;

public class AffirmPageConfigData {
    private String affirmPromotionPage;
    private String promotionMessage;
    private boolean showAffirmPromotionFlag;

    public String getAffirmPromotionPage() {
        return affirmPromotionPage;
    }

    public void setAffirmPromotionPage(String affirmPromotionPage) {
        this.affirmPromotionPage = affirmPromotionPage;
    }

    public String getPromotionMessage() {
        return promotionMessage;
    }

    public void setPromotionMessage(String promotionMessage) {
        this.promotionMessage = promotionMessage;
    }

    public boolean isShowAffirmPromotionFlag() {
        return showAffirmPromotionFlag;
    }

    public void setShowAffirmPromotionFlag(boolean showAffirmPromotionFlag) {
        this.showAffirmPromotionFlag = showAffirmPromotionFlag;
    }
}
