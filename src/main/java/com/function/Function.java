package com.function;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpTrigger-Java". Two ways to invoke
     * it using "curl" command in bash: 1. curl -d "HTTP Body" {your
     * host}/api/HttpTrigger-Java&code={your function key} 2. curl "{your
     * host}/api/HttpTrigger-Java?name=HTTP%20Query&code={your function key}"
     * Function Key is not needed when running locally, it is used to invoke
     * function deployed to Azure. More details:
     * https://aka.ms/functions_authorization_keys
     */
    @FunctionName("HttpTrigger-Java")
    public HttpResponseMessage run(@HttpTrigger(name = "req", methods = { HttpMethod.GET,
            HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("city");
        String city = request.getBody().orElse(query);

        // Create GET Request
        final CloseableHttpClient httpClient = HttpClients.createDefault();

        // If no city is provided, use "Paris"
        String cityUrl = (city != null ? city : "Paris");
        String skyFlightUrl = "https://skyscanner-skyscanner-flight-search-v1.p.rapidapi.com/apiservices/autosuggest/v1.0/UK/GBP/en-GB/?query="
                + cityUrl;

        HttpGet getRequest = new HttpGet(skyFlightUrl);
        
        // Add request header
        getRequest.addHeader("X-RapidAPI-Key", "5664466a8cmsh6d583c66d052ce0p15f49bjsn4d2a6186c3be");

        // Send request
        try (CloseableHttpResponse response = httpClient.execute(getRequest)) {

            // Get HttpResponse Status
            System.out.println(response.getStatusLine().toString());

            HttpEntity entity = response.getEntity();
            Header headers = entity.getContentType();
            System.out.println(headers);

            String result = EntityUtils.toString(entity);
            return request.createResponseBuilder(HttpStatus.OK).body(result).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Error calling Skyflight API").build();
        }

    }
}
