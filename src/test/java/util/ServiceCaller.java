package util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

import static util.LogWrapper.logError;

/**
 * Requests return a special class that holds the  response headers, status line, and body as we are using.
 * This is because of the design decision to use 'try with resource' so that HTTP connections are closed as soon as the request is completed (or actions fail).
 */
public class ServiceCaller {

    public static IServiceResponse getRequest(String uri) throws IOException {
        HttpGet request = new HttpGet(uri);

        // Add specific headers here if required. Example: request.setHeader("keyname", value);
        return executeRequest(request);
    }

    private static ServiceResponse executeRequest(HttpRequestBase request) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try (
                // Use a closable design to avoid leaving connections open, particularly if something unexpected happens.
                CloseableHttpResponse response = httpClient.execute(request)) {
            return new ServiceResponse(response);

        } catch (IOException e) {
            logError("Failed to complete execution of " + request.getMethod() + " request to: " + request.getURI());
            throw e;
        }
    }
}
