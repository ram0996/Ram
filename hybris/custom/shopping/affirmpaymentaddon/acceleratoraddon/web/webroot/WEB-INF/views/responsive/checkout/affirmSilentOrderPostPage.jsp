<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="multiCheckout" tagdir="/WEB-INF/tags/responsive/checkout/multi"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<spring:htmlEscape defaultHtmlEscape="true" />

<form:form id="silentOrderPostForm" class="js-card-payment" name="silentOrderPostForm" modelAttribute="sopPaymentDetailsForm" action="${paymentFormUrl}" method="POST" >
        <c:forEach items="${sopPaymentDetailsForm.parameters}" var="entry" varStatus="status">
            <input type="hidden" id="${fn:escapeXml(entry.key)}" name="${fn:escapeXml(entry.key)}" value="${fn:escapeXml(entry.value)}"/>
        </c:forEach>
        <c:forEach items="${sopPaymentDetailsForm.subscriptionSignatureParams}" var="entry" varStatus="status">
            <input type="hidden" id="${fn:escapeXml(entry.key)}" name="${fn:escapeXml(entry.key)}" value="${fn:escapeXml(entry.value)}"/>
        </c:forEach>

        <input type="hidden" id="card_cardType" name="card_cardType" value="001"/>
        <input type="hidden" id="card_nameOnCard" name="card_nameOnCard" />
        <input type="hidden" id="card_accountNumber" name="card_accountNumber" />
        <input type="hidden" id="card_expirationMonth" name="card_expirationMonth" />
        <input type="hidden" id="card_expirationYear" name="card_expirationYear" />
        <input type="hidden" id="card_cvNumber" name="card_cvNumber" />


        <input type="hidden" name="affirm_vcn" value="true" />
        <input type="hidden" id="affirm_id" name="affirm_id" value="" />
        <input type="submit" value="Submit" />
</form:form>
