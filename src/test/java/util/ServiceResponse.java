package util;

import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static util.LogWrapper.logError;

/**
 * Used to capture api http/s response details so that they can be validated.
 * Including an interface allows flexibility in the choice of HTTP implementation classes.
 * This implementation supports use of the apache.http closeable implementation.
 */

public class ServiceResponse implements IServiceResponse {

    private final StatusLine statusLine;
    private final Map<String, Object> headers;
    private final String rawResponse;

    public ServiceResponse(CloseableHttpResponse response) throws IOException {
        this.statusLine = response.getStatusLine();

        // Convert header object to a more common format for easier validation processing.
        Map<String, Object> tempHeaders = new HashMap<>();
        Arrays.stream(response.getAllHeaders()).toList()
                .forEach(header -> tempHeaders.put(header.getName(), header.getValue()));
        this.headers = tempHeaders;

        // Capture the body as is.
        try {
            this.rawResponse = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            logError("Error converting api service response body: \n" + e.getMessage());
            throw e;
        }
    }

    @Override
    public int getStatusCode() {
        return statusLine.getStatusCode();
    }

    @Override
    public String getStatusText() {
        return statusLine.getReasonPhrase();
    }

    @Override
    public Map<String, Object> getHeaders() {
        return headers;
    }

    @Override
    public String getBody() {
        return rawResponse;
    }
}
