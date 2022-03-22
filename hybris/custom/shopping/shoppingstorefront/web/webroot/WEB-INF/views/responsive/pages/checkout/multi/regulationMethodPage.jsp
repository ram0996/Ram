<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address"%>
<%@ taglib prefix="multi-checkout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<template:page pageTitle="${pageTitle}" hideHeaderLinks="true">

<div class="row">
    <div class="col-sm-6">
        <div class="checkout-headline">
            <span class="glyphicon glyphicon-lock"></span>
            <spring:theme code="checkout.multi.secure.checkout" />
        </div>
		<multi-checkout:checkoutSteps checkoutSteps="${checkoutSteps}" progressBarId="${progressBarId}">
			<jsp:body>
				<ycommerce:testId code="checkoutStepThree">
					<form action="${request.contextPath}/checkout/multi/regulation-method/validate" method="GET" class="wrapper-checkout" id="regulation-form">
					<div class="checkout-regulation">
						Is Military Selection Applicable? <br>
						<div class="radio-button">
						<c:choose>
							<c:when test="${not empty cartData.militaryFlag && cartData.militaryFlag eq 'Yes'}">
								<input type="radio" name="militaryFlag" value="Yes" checked><spring:theme code="regulation.yes"/>
							</c:when>
							<c:otherwise>
								<input type="radio" name="militaryFlag" value="Yes"/><spring:theme code="regulation.yes"/>
							</c:otherwise>
						</c:choose>
						</div>
						<div class="radio-button">
						<c:choose>
							<c:when test="${not empty cartData.militaryFlag && cartData.militaryFlag eq 'No'}">
								<input type="radio" name="militaryFlag" value="No" checked><spring:theme code="regulation.no"/>
							</c:when>
							<c:otherwise>
								<input type="radio" name="militaryFlag" value="No"/><spring:theme code="regulation.no"/>
							</c:otherwise>
						</c:choose>
						</div>
						
						
					</div>
					<button id="regulationMethodSubmit" type="button" class="btn btn-primary btn-block checkout-next"><spring:theme code="checkout.multi.deliveryMethod.continue"/></button>
					</form>
				</ycommerce:testId>
			</jsp:body>
		</multi-checkout:checkoutSteps>
    </div>

    <div class="col-sm-6 hidden-xs">
		<multi-checkout:checkoutOrderDetails cartData="${cartData}" showDeliveryAddress="true" showPaymentInfo="false" showTaxEstimate="false" showTax="true" />
    </div>

    <div class="col-sm-12 col-lg-12">
        <cms:pageSlot position="SideContent" var="feature" element="div" class="checkout-help">
            <cms:component component="${feature}"/>
        </cms:pageSlot>
    </div>
</div>

</template:page>
