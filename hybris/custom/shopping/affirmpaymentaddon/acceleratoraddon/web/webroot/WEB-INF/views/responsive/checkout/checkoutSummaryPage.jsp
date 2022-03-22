<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<spring:htmlEscape defaultHtmlEscape="true" />

<spring:url value="/checkout/multi/summary/placeOrder" var="placeOrderUrl" htmlEscape="false"/>
<spring:url value="/checkout/multi/termsAndConditions" var="getTermsAndConditionsUrl" htmlEscape="false"/>

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

    <jsp:attribute name="pageScripts">
    </jsp:attribute>
    <jsp:body>
        <div class="row affirm-checkout">
            <div class="col-sm-6">
                <div class="checkout-headline">
                    <span class="glyphicon glyphicon-lock"></span>
                    <spring:theme code="checkout.multi.secure.checkout" />
                </div>
                <multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
                    <ycommerce:testId code="checkoutStepFour">
                        <div class="checkout-review hidden-xs">
                            <div class="checkout-order-summary">
                                <multi-checkout:orderTotals cartData="${cartData}" showTaxEstimate="${showTaxEstimate}" showTax="${showTax}" subtotalsCssClasses="dark"/>
                            </div>
                        </div>
                        <div class="place-order-form hidden-xs">
                            <c:choose>
                                <c:when test="${empty cartData.paymentInfo or not empty cartData.paymentInfo.affirmVCNId}">
                                    <span>You will be redirected to Affirm to securely complete your purchase. Just fill out a few pieces of basic information and get a real-time decision. Checking your eligibility won't affect your credit score.</span>
                                    <button type="button" class="js-affirm-payment btn btn-primary btn-block js-affirm-pay-button"  data-vcnmode="${affirmVcnMode}">Checkout with affirm</button>
                                </c:when>
                                <c:otherwise>
                                    <form:form action="${placeOrderUrl}" id="placeOrderForm1" modelAttribute="placeOrderForm">
                                        <div class="checkbox">
                                            <label> <form:checkbox id="Terms1" path="termsCheck" />
                                                <spring:theme var="termsAndConditionsHtml" code="checkout.summary.placeOrder.readTermsAndConditions" arguments="${fn:escapeXml(getTermsAndConditionsUrl)}" htmlEscape="false"/>
                                                ${ycommerce:sanitizeHTML(termsAndConditionsHtml)}
                                            </label>
                                        </div>

                                        <button id="placeOrder" type="submit" class="btn btn-primary btn-place-order btn-block">
                                            <spring:theme code="checkout.summary.placeOrder" text="Place Order"/>
                                        </button>

                                    </form:form>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </ycommerce:testId>
                </multi-checkout:checkoutSteps>
            </div>

            <div class="col-sm-6">
                <multi-checkout:checkoutOrderSummary cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="true" showTaxEstimate="true" showTax="true" />
            </div>

            <div class="col-sm-12 col-lg-12">
                <br class="hidden-lg">
                <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
                    <cms:component component="${feature}"/>
                </cms:pageSlot>
            </div>

            <div id="js-affirm-form" >

            </div>
        </div>
    </jsp:body>
</template:page>
