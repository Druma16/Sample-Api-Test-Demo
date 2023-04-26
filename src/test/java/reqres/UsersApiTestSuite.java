package reqres;

import com.google.gson.JsonObject;
import net.javacrumbs.jsonunit.core.Option;
import net.javacrumbs.jsonunit.core.listener.Difference;
import org.junit.jupiter.api.*;
import util.IServiceResponse;
import util.RecordingDifferenceListener;

import java.io.IOException;
import java.util.List;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.*;
import static reqres.model.GetUsersDataProvider.*;
import static util.JsonConvertor.getJsonFromString;
import static util.LogWrapper.logError;
import static util.LogWrapper.logInfo;
import static util.ServiceCaller.getRequest;


/**
 * Design Note: By using interfaces with default methods we can bring multiple, as-needed helper methods into a testsuite
 * (ie 'mixin) without locking in a fixed inheritance hierarchy.
 * Parent class (e.g. BaseTestSuite) can be useful for carefully curated, truly common hierarchical needs such as suite-universal setup /teardown tasks.
 */
public class UsersApiTestSuite implements ITestValidation {

    private final RecordingDifferenceListener diffListener = new RecordingDifferenceListener();
    private List<Difference> differenceList = diffListener.getDifferenceList();

    @BeforeAll
    public static void testSuiteSetup() {
        // TODO Include Basic service check such as is an entry endpoint alive and responsive
        // May include logging handling here - output names /location etc.
    }

    @BeforeEach
    public void beforeEachTest() {
        // Reset the list of differences for logging so that previous test results not included.
        differenceList = diffListener.getDifferenceList();
    }

    @AfterEach
    public void afterEachTest() {
        // Log full list of any differences found in the response body
        if (differenceList.size() > 0) {
            logError("FAILURE: Differences between the expected and actual JSON response body found as follows: \n");
            for (Difference difference : differenceList) {
                logInfo("\n" + difference.toString());
                logInfo(String.format("Expected Path: %s Actual Path: %s", difference.getExpectedPath(), difference.getActualPath()));
                logInfo(String.format("Expected Value: %s Actual Value: %s\n", difference.getExpected(), difference.getActual()));
            }
        }
    }

    /**
     * Note: This test allows the API response to list users in any order so long as the same page of results is returned.
     * A Dynamic Value example has been included in 'model/getUsersExpectedResponse.json' line 19.
     */
    @Test
    @DisplayName("List Users Api Test - Default Positive Case")
    public void listUsersPositiveTest() throws IOException {
        /* TODO Check in setup phase of test for existence and valid conversion of expected results file
            - report failure in this step as an aborted (failed assumption)
            - rather than test fail result as it does not indicate an issue with the api under test. */
        JsonObject expectedJson = getDefaultPositiveResponseBody();

        logInfo("Starting Get all users positive test - calling the api");
        IServiceResponse serviceResponse = getRequest(getAllUsersUri());

        logInfo("Call to get all users responded with: " + serviceResponse.getStatusCode() + "\n"
                + serviceResponse.getHeaders().toString() + "\n" + serviceResponse.getBody());

        /* Let the test fail here if the status is incorrect -
           headers and response body are unlikely to be correct, reduce noise for triage.
         */
        assertEquals(200, serviceResponse.getStatusCode(), "HTTP/S Response code");

        /* Validate both the headers and the body content independently so that
           every test execution provides as much information (both pass and fail) as possible.
           It would not be efficient to find and fix a header issue only to then find a content issues and vica versa.
        */
        assertAll(
                () -> assertEquals(serviceResponse.getStatusText().toLowerCase(), "ok", "HTTP Status Message"),
                /* Note: extra headers in the actual received are ignored -
                   if these will cause integration issues include them in test.
               */
                () -> checkExpectedHeaders(serviceResponse.getHeaders(), getPositiveResponseExpectedHeaders()),
                () -> {
                    JsonObject actualJson = getJsonFromString(serviceResponse.getBody());
                    /* Note: dynamic body values are handled by denoting them in the expected json as a value
                       like '${json-unit.regex}<pattern>'".
                     */
                    assertThatJson(actualJson)
                            // Note: let the results be in any order, unless list order is part of the api design spec.
                            .when(Option.IGNORING_ARRAY_ORDER)
                            .withDifferenceListener(diffListener)
                            .withThreadDumpOnError()
                            .isEqualTo(expectedJson);
                }
        );
    }


    @Test
    @DisplayName("List User (Single) Api Test - Deliberate Failing Case")
    public void listUsersDeliberateFailTest() throws IOException {
        JsonObject expectedJson = getDeliberateFailResponseBody();

        logInfo("Starting Get all users positive test - calling the api");
        IServiceResponse serviceResponse = getRequest(getAllUsersUri());

        logInfo("Call to get all users responded with: " + serviceResponse.getStatusCode() + "\n"
                + serviceResponse.getHeaders().toString() + "\n" + serviceResponse.getBody());

        // Note: we are failing the headers and body content, not the return status.
        assertEquals(200, serviceResponse.getStatusCode(), "HTTP/S Response code");

        assertAll(
                () -> assertEquals(serviceResponse.getStatusText().toLowerCase(), "ok", "HTTP Status Message"),
                // Note:both failures in headers and body are included - all should be logged.
                () -> checkExpectedHeaders(serviceResponse.getHeaders(), getDeliberateFailResponseHeaders()),
                () -> {
                    JsonObject actualJson = getJsonFromString(serviceResponse.getBody());
                    assertThatJson(actualJson)
                            .when(Option.IGNORING_ARRAY_ORDER)
                            .withDifferenceListener(diffListener)
                            .withThreadDumpOnError()
                            .isEqualTo(expectedJson);
                }
        );
    }
}
