package util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogWrapper {

    private static final Logger logger = Logger.getLogger("global");

    // Useful when we want to extend the logger with multiple outputs (handlers) eg .txt files.
    public static Logger getLogger(){
        return logger;
    }


    public static void logInfo(String message){
        // Print to IDE console, in case the IDE in use does not receive the test logger output.
        System.out.println(message);
        logger.log(Level.INFO, message);
    }

    public static void logError(String message){
        System.err.println(message);
        logger.log(Level.SEVERE, message);
    }
}

