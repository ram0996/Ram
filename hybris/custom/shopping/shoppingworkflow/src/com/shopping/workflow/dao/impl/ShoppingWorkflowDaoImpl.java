/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.shopping.workflow.dao.impl;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;

import com.shopping.workflow.dao.ShoppingWorkflowDao;

/**
 *
 */
public class ShoppingWorkflowDaoImpl implements ShoppingWorkflowDao
{
	private static final String FETCH_PRODUCT_CATALOGS = "SELECT {P." + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE
			+ " AS P JOIN " + CatalogVersionModel._TYPECODE + " AS cv on {P." + ProductModel.CATALOGVERSION + "}={cv."
			+ CatalogVersionModel.PK + "}" + " JOIN " + CatalogModel._TYPECODE + " AS c on {cv." + CatalogVersionModel.CATALOG
			+ "}={c." + CatalogModel.PK + "}} WHERE {c." + CatalogModel.ID + "}='shoppingProductCatalog'" + " AND {cv."
			+ CatalogVersionModel.VERSION + "}='Staged' AND {P." + ProductModel.APPROVALSTATUS + "} IN ({{SELECT {PK} FROM {"
			+ ArticleApprovalStatus._TYPECODE + "} WHERE {code} = 'check'}})";

	private static final String FIND_WORKFLOW_TEMPLATE = "SELECT {" + WorkflowTemplateModel.PK + "} FROM {"
			+ WorkflowTemplateModel._TYPECODE + "} WHERE {" + WorkflowTemplateModel.CODE + "}=?code";


	@Resource(name = "flexibleSearchService")
	private FlexibleSearchService flexibleSearchService;

	@Override
	public List<ItemModel> getAllProductsForCheckStatus()
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FETCH_PRODUCT_CATALOGS);
		final SearchResult<ItemModel> results = flexibleSearchService.search(query);
		if (Objects.nonNull(results) && CollectionUtils.isNotEmpty(results.getResult()))
		{
			return results.getResult();
		}
		return null;
	}

	@Override
	public List<ItemModel> getItemsListForQuery(final String stringQuery)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(stringQuery);
		final SearchResult<ItemModel> results = flexibleSearchService.search(query);
		if (Objects.nonNull(results) && CollectionUtils.isNotEmpty(results.getResult()))
		{
			return results.getResult();
		}
		return null;
	}

	@Override
	public WorkflowTemplateModel getWorkflowTemplateForCode(final String code)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(FIND_WORKFLOW_TEMPLATE);
		query.addQueryParameter("code", code);
		final SearchResult<WorkflowTemplateModel> results = flexibleSearchService.search(query);
		if (Objects.nonNull(results) && CollectionUtils.isNotEmpty(results.getResult()))
		{
			return results.getResult().get(0);
		}
		return null;
	}

}
