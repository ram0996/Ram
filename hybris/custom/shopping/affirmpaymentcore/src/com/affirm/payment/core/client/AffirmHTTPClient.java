package com.affirm.payment.core.client;

import com.affirm.payment.model.AffirmConfigContainerModel;

/**
 * HTTP client class to send the JSON request to Affirm
 */
public interface AffirmHTTPClient {

   /**
    * Send the given json string to the specified endpoint
    * @param endpointUrl The server endpoint where the json will be sent
    * @param jsonString The JSON request to be sent
    * @param affirmConfigContainer Affirm configuration container to hold the authentication information
    * @return Retunrs the response JSON returned by the server.
    */
   String send(String endpointUrl, String jsonString, AffirmConfigContainerModel affirmConfigContainer);

}
