package airAstana.flightStatus.configuration;

import lombok.Getter;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * manages the configuration and initialization of application's logging using Java Util Logging.
 */
public class LoggerManager {
    @Getter
    private static final Logger logger = Logger.getLogger(LoggerManager.class.getName());

    static {
        try {
            LogManager.getLogManager().readConfiguration(LoggerManager.class.getResourceAsStream("/logging/logging.properties"));

            FileHandler fileHandler = new FileHandler("application.log", true);
            fileHandler.setFormatter(new SimpleFormatter());

            logger.addHandler(fileHandler);
            logger.setLevel(java.util.logging.Level.INFO);
        } catch (IOException e) {
            logger.severe("could not init a logger: " + e.getMessage());
        }
    }
}
