package reqres;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static util.LogWrapper.logError;

/** Design Note: Common test helper class (applying mixin pattern) for reuse between test suites
   (anticipating there will be more than one set of tests).
 */
public interface ITestValidation {

    String DYNAMIC_HEADER_INDICATOR = "matchesPattern:";

    default void checkExpectedHeaders(Map<String, Object> actualHeaders, Map<String, Object> expectedHeaders) {
        // Store all the failures for logging.
        List<String> incorrectEntries = new ArrayList<>();

        Set<Map.Entry<String, Object>> entries = expectedHeaders.entrySet();
        entries.forEach(expectedEntry -> {

            // First check header name exists in actual.
            String expectedHeaderName = expectedEntry.getKey();
            Object actualValue = actualHeaders.get(expectedHeaderName);
            if (actualValue == null) {
                incorrectEntries.add("Missing header - expected: " + expectedHeaderName + " not found in actual headers received.");
                return;
            }
            Object expectedValue = expectedEntry.getValue();

            if (expectedValue.toString().startsWith(DYNAMIC_HEADER_INDICATOR)) {
                // DO pattern match
                String pattern = expectedValue.toString().replace(DYNAMIC_HEADER_INDICATOR, "");
                if (!dynamicValueMatches(pattern, expectedHeaderName, actualValue)) {
                    incorrectEntries.add(String.format("Dynamic Header value for '%s' does not match pattern -: %s \tactual value: %s",
                            expectedHeaderName, pattern, actualValue));
                }
            } else {
                // DO Exact Match
                if (!expectedValue.equals(actualValue)) {
                    incorrectEntries.add(String.format("Header value for '%s' does not exact match - expected value: %s \tactual value: %s",
                            expectedHeaderName, expectedValue, actualValue));
                }
            }
        });

        assertFalse(incorrectEntries.size() > 0,
                "FAILURE: Expected Headers did not match Actual - missing or mismatched values as follows:\n" +
                        StringUtils.join(incorrectEntries, "\n"));
    }

    private boolean dynamicValueMatches(String pattern, String expectedHeaderName, Object actualValue) {
        try {
            Matcher matcher = Pattern.compile(pattern).matcher(actualValue.toString());
            return matcher.matches();
        } catch (PatternSyntaxException pse) {
            logError(String.format(
                    "Error in test data - could not extract a match pattern from dynamic header %s with pattern %s using indicator string: %s",
                    expectedHeaderName, pattern, DYNAMIC_HEADER_INDICATOR));
            throw pse;
        }
    }
}
