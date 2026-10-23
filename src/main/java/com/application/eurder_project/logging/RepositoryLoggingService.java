package com.application.eurder_project.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class RepositoryLoggingService {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryLoggingService.class);

    public static void traceLog(String msg) {
        logger.trace(msg);
    }

    public static void getAllLog(int size, String className) {
        traceLog("Fetching all items (" + size + " " + className + " stored).");
    }

    public static void getOneLog(UUID id, String className) {
        traceLog("Fetching item " + id + " of the class " + className + ".");
    }

    public static void SaveLog(UUID id, String className) {
        traceLog("Saving item " + id + " in " + className + " repository.");
    }

    public static void UpdateLog(UUID id, String className) {
        traceLog("Updating item " + id + " in " + className + " repository.");
    }

    public static void DeleteLog(UUID id, String className) {
        traceLog("Delete item " + id + " in " + className + " repository.");
    }


}
