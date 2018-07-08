package ru.obolensk.afff.beetle2.log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.slf4j.LoggerFactory;

/**
 * Wrapper for sl4j logger
 * Created by Afff on 05.06.2016.
 */
public class Logger {

    private final org.slf4j.Logger sl4jLogger;

    private final long id;

    public Logger(Class<?> clazz) {
        sl4jLogger = LoggerFactory.getLogger(clazz);
        id = -1;
    }

    public Logger(Class<?> clazz, long objectId) {
        sl4jLogger = LoggerFactory.getLogger(clazz);
        id = objectId;
    }

    /**
     * Method enables logging to system.out
     */
    public static void addConsoleAppender(@Nonnull final Level level) {
        ConsoleAppender console = new ConsoleAppender();
        String PATTERN = "%d [%p] {%c{1}} %m%n";
        console.setLayout(new PatternLayout(PATTERN));
        console.setThreshold(level);
        console.activateOptions();
        org.apache.log4j.Logger.getRootLogger().addAppender(console);
        org.apache.log4j.Logger.getRootLogger().setLevel(level);
    }

    public void error(String message, Object... params) {
        sl4jLogger.error(prepend(message), params);
    }

    public void error(String message, Throwable throwable) {
        sl4jLogger.error(prepend(message), throwable);
    }

    public void error(Throwable err) {
        sl4jLogger.error(prepend(err.getMessage()), err);
    }

    public void info(String message, Object... params) {
        sl4jLogger.info(prepend(message), params);
    }

    public void info(String message, Throwable throwable) {
        sl4jLogger.info(prepend(message), throwable);
    }

    public void warn(String message, Object... params) {
        sl4jLogger.warn(prepend(message), params);
    }

    public void warn(String message, Throwable throwable) {
        sl4jLogger.warn(prepend(message), throwable);
    }

    public void debug(String message, Object... params) {
        sl4jLogger.debug(prepend(message), params);
    }

    public void debug(String message, Throwable throwable) {
        sl4jLogger.debug(prepend(message), throwable);
    }

    public void debug(Throwable err) {
        sl4jLogger.debug(prepend(err.getMessage()), err);
    }

    public void trace(Throwable err) {
        sl4jLogger.trace(prepend(err.getMessage()), err);
    }

    public void trace(String message, Object... params) {
        sl4jLogger.trace(prepend(message), params);
    }

    public void trace(String message, Throwable throwable) {
        sl4jLogger.trace(prepend(message), throwable);
    }

    @Nullable
    private String prepend(@Nullable String message) {
        if (id != -1) {
            return id + ": " + message;
        } else {
            return message;
        }
    }
}
