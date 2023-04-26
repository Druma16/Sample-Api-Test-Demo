package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static util.LogWrapper.logError;
import static util.LogWrapper.logInfo;

/** Supports conversions of strings or input file contents to a standard Json Object -
 *  So that expected and actual Json responses can be effectively and completely validated.
 */
public class JsonConvertor {

    private static final Gson gson = new GsonBuilder()
            .serializeNulls() // includes nulls in output
            .setPrettyPrinting()
            .create();


    public static JsonObject getJsonFromString(String jsonAsString) {
        try {
            return gson.fromJson(jsonAsString, JsonObject.class);
        } catch (JsonSyntaxException e) {
            logError("Error when converting string to Json Object: " + jsonAsString);
            throw e;
        }
    }

    public static JsonObject getJsonFromFile(Path inputFilePath) throws IOException {
        logInfo("Loading JSON input file: " + inputFilePath);
        String rawResponse = "";
        try {
            List<String> inputLines = Files.readAllLines(inputFilePath, StandardCharsets.UTF_8);
            rawResponse = inputLines.stream().map(String::trim).collect(Collectors.joining());
            return gson.fromJson(rawResponse, JsonObject.class);

        } catch (FileNotFoundException fnfe) {
            logError("Error with finding input file: " + inputFilePath);
            throw fnfe;
        } catch (IOException ioe) {
            logError("Error when reading input file: " + inputFilePath);
            throw ioe;
        } catch (JsonSyntaxException e) {
            logError("Error when converting file contents to Json Object: " + rawResponse);
            throw e;
        }
    }
}
