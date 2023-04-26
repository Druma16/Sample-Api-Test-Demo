package util;

import java.util.Map;

public interface IServiceResponse {

    int getStatusCode();

    String getStatusText();

    Map<String,Object> getHeaders();

    String getBody(); // Could be json or xml or any response text.

}
