package reqres.model;

import com.google.gson.JsonObject;
import reqres.ITestValidation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static util.JsonConvertor.getJsonFromFile;


/**
 * Provide the expected details for a test to validate - Status Code, Headers, Body Content.
 * Design Note: this may move away from static implementations to an instantiated class design when we include functionality like verifying a
 * correctly formatted (and existing) input file is supplied as part of pre-test conditions.
 */
public class GetUsersDataProvider implements ITestValidation {

    public static String getAllUsersUri() {
        // TODO Move such details to an environment setting (dotenv style) and instantiate from the environment instead of hard-code.
        return "https://reqres.in/api/users";
    }

    public static JsonObject getDefaultPositiveResponseBody() throws IOException {
        Path expectedJsonFilePath = Paths.get("src", "test", "java", "reqres", "model", "getUsersExpectedResponse.json");
        return getJsonFromFile(expectedJsonFilePath);
    }

    /**
     * Design Note: Not all headers are included - this is because many systems add their own headers,
     * and without knowing the architecture of others who may execute this test,
     * I have attempted to prevent false failures by only included likely repeatable headers.
     */
    public static Map<String, Object> getPositiveResponseExpectedHeaders() {
        //Note Map.of convenience method has a max init of 10 - change if more required.
        return Map.of(
                // Expected Exact Values
                "Content-Type", "application/json; charset=utf-8",
                "Transfer-Encoding", "chunked",
                "Connection", "keep-alive",
                "X-Powered-By", "Express",
                "Access-Control-Allow-Origin", "*",
                "Server", "cloudflare",

                // Expected Dynamic Values
                // like Sat, 22 Apr 2023 03:47:43 GMT
                "Date", DYNAMIC_HEADER_INDICATOR + "[A-Za-z]{3}.\\s\\d{1,2}\\s[A-Za-z]{3}\\s\\d{4}\\s\\d{2}:\\d{2}:\\d{2}\\s[A-Z]{3}",
                "Age", DYNAMIC_HEADER_INDICATOR + "\\d+" // like Age: 1858
        );
    }


    // Deliberate Failure data so that we can showcase a negative result in logging / reporting.

    public static JsonObject getDeliberateFailResponseBody() throws IOException {
        // Includes 2 failures - mismatch field value, and extra field in actual (name for id=3 removed in expected).
        Path expectedJsonFilePath = Paths.get("src", "test", "java", "reqres", "model", "getUsersBadResponse.json");
        return getJsonFromFile(expectedJsonFilePath);
    }

    public static Map<String, Object> getDeliberateFailResponseHeaders() {
        //Note Map.of convenience method has a max init of 10 - change if more required.
        return Map.of(
                // Expected Exact Values
                "Content-Type", "application/xml",  // deliberate mismatch value
                "Transfer-Encoding", "chunked",
                "Connection", "keep-alive",
                "X-Powered-By", "Express",
                "Access-Control-Allow-Origin", "*",
                "Server", "cloudflare",
                "Missing-Header", "Faked", // faked missing header example

                // Expected Dynamic Values
                // like Sat, 22 Apr 2023 03:47:43 GMT
                "Date", DYNAMIC_HEADER_INDICATOR + "[A-Za-z]{3}.\\s\\d{1,2}\\s[A-Za-z]{3}\\s\\d{4}\\s\\d{2}:\\d{2}:\\d{2}\\s[A-Z]{3}",
                "Age", DYNAMIC_HEADER_INDICATOR + "[a-z]+" // deliberate wrong pattern - should be like Age: 1858
        );
    }

}
