/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.shopping.workflow.email.service.impl;

import de.hybris.platform.acceleratorservices.email.CMSEmailPageService;
import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageTemplateModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.mail.MailUtils;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.shopping.workflow.email.context.ShoppingProductApprovalWorkflowEmailContext;
import com.shopping.workflow.email.context.ShoppingWorkflowEmailContext;
import com.shopping.workflow.email.service.ShoppingEmailService;


/**
 *
 */
public class ShoppingEmailServiceImpl implements ShoppingEmailService
{
	private static final Logger LOG = LoggerFactory.getLogger(ShoppingEmailServiceImpl.class);

	@Resource(name = "rendererService")
	private RendererService rendererService;

	@Resource(name = "shoppingWorkflowEmailContext")
	private ShoppingWorkflowEmailContext shoppingWorkflowEmailContext;

	@Resource(name = "shoppingProductApprovalWorkflowEmailContext")
	private ShoppingProductApprovalWorkflowEmailContext shoppingProductApprovalWorkflowEmailContext;

	@Resource(name = "catalogService")
	private CatalogService catalogService;

	@Resource(name = "emailService")
	private EmailService emailService;

	@Resource(name = "cmsEmailPageService")
	private CMSEmailPageService cmsEmailPageService;

	@Resource(name = "userService")
	private UserService userService;

	@Override
	public void sendEmailForNewPartApproval(final ProductModel part, final UserModel user)
	{
		final RendererTemplateModel subjectTemplate = rendererService.getRendererTemplateForCode("newPartApprovalSubject");
		final RendererTemplateModel bodyTemplate = rendererService.getRendererTemplateForCode("newPartApprovalBody");
		shoppingWorkflowEmailContext.init(part, user);
		//Create a writer where rendered text will be written to
		final StringWriter subjectMessage = new StringWriter();
		final StringWriter bodyMessage = new StringWriter();

		//Render the template using the context object
		rendererService.render(subjectTemplate, shoppingWorkflowEmailContext, subjectMessage);
		rendererService.render(bodyTemplate, shoppingWorkflowEmailContext, bodyMessage);

		try
		{
			final HtmlEmail htmlEmail = (HtmlEmail) MailUtils.getPreConfiguredEmail();
			htmlEmail.setCharset("UTF-8");
			htmlEmail.addTo(user.getUid());
			htmlEmail.setFrom(shoppingWorkflowEmailContext.getFromEmail());
			htmlEmail.setSubject(subjectMessage.toString());
			htmlEmail.setHtmlMsg(bodyMessage.toString());
			htmlEmail.send();
		}
		catch (final EmailException e)
		{
			LOG.error("While sending an email to new part approval user ", e);
		}

	}


	public void sendEmailToApprover(final List<ItemModel> getAllProducts)
	{
		final CatalogVersionModel catalog = catalogService.getCatalogVersion("shoppingContentCatalog", "Online");

		final EmailPageModel emailPageModel = cmsEmailPageService.getEmailPageForFrontendTemplate("productApprovalWorkflowTemplate",
				catalog);
		if (Objects.nonNull(emailPageModel))
		{
			shoppingProductApprovalWorkflowEmailContext.init(getAllProducts, userService.getUserForUID("globalproductauthor"));
			final EmailPageTemplateModel emailPageTemplateModel = (EmailPageTemplateModel) emailPageModel.getMasterTemplate();
			final RendererTemplateModel bodyRenderTemplate = emailPageTemplateModel.getHtmlTemplate();
			Assert.notNull(bodyRenderTemplate, "HtmlTemplate associated with MasterTemplate of EmailPageModel cannot be null");
			final RendererTemplateModel subjectRenderTemplate = emailPageTemplateModel.getSubject();
			Assert.notNull(subjectRenderTemplate, "Subject associated with MasterTemplate of EmailPageModel cannot be null");

			final StringWriter subject = new StringWriter();
			rendererService.render(subjectRenderTemplate, shoppingProductApprovalWorkflowEmailContext, subject);

			final StringWriter body = new StringWriter();
			rendererService.render(bodyRenderTemplate, shoppingProductApprovalWorkflowEmailContext, body);

			final EmailMessageModel emailMessageModel = createEmailMessage(subject.toString(), body.toString(),
					shoppingProductApprovalWorkflowEmailContext, emailPageModel);

			emailService.send(emailMessageModel);

		}

	}

	protected EmailMessageModel createEmailMessage(final String emailSubject, final String emailBody,
			final ShoppingProductApprovalWorkflowEmailContext emailContext, final EmailPageModel emailPageModel)
	{
		final List<EmailAddressModel> toEmails = new ArrayList<EmailAddressModel>();
		final EmailAddressModel toAddress = emailService.getOrCreateEmailAddressForEmail("venkat05.hybris@gmail.com",
				"Venkata Ramana");
		toEmails.add(toAddress);
		final EmailAddressModel fromAddress = emailService.getOrCreateEmailAddressForEmail(emailPageModel.getFromEmail(),
				emailPageModel.getFromName());
		return emailService.createEmailMessage(toEmails, new ArrayList<EmailAddressModel>(), new ArrayList<EmailAddressModel>(),
				fromAddress, emailPageModel.getFromEmail(), emailSubject, emailBody, null);
	}


}
