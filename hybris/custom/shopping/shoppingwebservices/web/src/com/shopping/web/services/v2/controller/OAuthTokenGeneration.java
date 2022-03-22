/**
 *
 */
package com.shopping.web.services.v2.controller;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


/**
 * @author venka
 *
 */
@RequestMapping(path = "/get-oauth-details")
public class OAuthTokenGeneration
{
	@Autowired
	private RestTemplate restTemplate;

	@GetMapping("/token")
	public String generateOauthToken(final HttpServletRequest request) throws RestClientException, URISyntaxException
	{
		final String client_id = "shopping";
		final String client_secret = "shop";
		final String grant_type = "client_credentials";
		final String uri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ "/authorizationserver/oauth/token?client_id=" + client_id + "&client_secret=" + client_secret + "&grant_type="
				+ grant_type + "";

		return restTemplate.postForObject(new URI(uri), request, String.class);

	}
}
