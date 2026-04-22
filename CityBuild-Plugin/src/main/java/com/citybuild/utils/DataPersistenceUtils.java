package com.citybuild.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.*;
import java.util.logging.Logger;

/**
 * Utility class for file I/O and JSON persistence operations
 * Provides safe loading and saving with proper error handling
 */
public class DataPersistenceUtils {
    private static final Logger logger = Logger.getLogger("CityBuild");

    /**
     * Safely load JSON from file with error handling
     */
    public static JsonObject loadJSON(File file) {
        if (file == null) {
            logger.warning("Attempted to load JSON from null file!");
            return new JsonObject();
        }

        if (!file.exists()) {
            logger.info("File does not exist (new file will be created on save): " + file.getName());
            return new JsonObject();
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            logger.fine("Successfully loaded JSON from: " + file.getName());
            return json;
        } catch (FileNotFoundException e) {
            logger.warning("JSON file not found (will create new): " + file.getName());
            return new JsonObject();
        } catch (IOException e) {
            logger.severe("IOException while reading JSON from " + file.getName() + ": " + e.getMessage());
            return new JsonObject();
        } catch (Exception e) {
            logger.severe("Failed to parse JSON from " + file.getName() + ": " + e.getMessage());
            e.printStackTrace();
            return new JsonObject();
        }
    }

    /**
     * Safely save JSON to file with error handling
     */
    public static boolean saveJSON(File file, JsonObject json, Gson gson) {
        if (file == null || json == null || gson == null) {
            logger.warning("Invalid parameters for saveJSON!");
            return false;
        }

        try {
            // Ensure parent directory exists
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    logger.warning("Failed to create parent directories for: " + file.getAbsolutePath());
                    return false;
                }
            }

            // Write to temp file first, then rename (atomic operation)
            File tempFile = new File(file.getParentFile(), file.getName() + ".tmp");
            
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(gson.toJson(json));
                writer.flush();
            }

            // Rename temp file to actual file
            if (!tempFile.renameTo(file)) {
                logger.warning("Failed to rename temp file to: " + file.getName());
                return false;
            }

            logger.fine("Successfully saved JSON to: " + file.getName());
            return true;
        } catch (IOException e) {
            logger.severe("IOException while writing JSON to " + file.getName() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            logger.severe("Unexpected error while saving JSON to " + file.getName() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create directory safely
     */
    public static boolean createDirectory(File dir) {
        if (dir == null) {
            logger.warning("Cannot create null directory!");
            return false;
        }

        if (dir.exists()) {
            if (!dir.isDirectory()) {
                logger.warning("Path exists but is not a directory: " + dir.getAbsolutePath());
                return false;
            }
            return true;
        }

        try {
            if (!dir.mkdirs()) {
                logger.warning("Failed to create directory: " + dir.getAbsolutePath());
                return false;
            }
            logger.fine("Created directory: " + dir.getAbsolutePath());
            return true;
        } catch (SecurityException e) {
            logger.severe("Permission denied creating directory: " + dir.getAbsolutePath());
            return false;
        }
    }

    /**
     * Safe file backup before overwrite
     */
    public static boolean backupFile(File original) {
        if (original == null || !original.exists()) {
            return true; // No backup needed if file doesn't exist
        }

        try {
            File backup = new File(original.getParent(), original.getName() + ".bak");
            
            // Only keep one backup
            if (backup.exists()) {
                if (!backup.delete()) {
                    logger.warning("Could not delete old backup: " + backup.getName());
                }
            }

            // Copy original to backup
            try (FileInputStream fis = new FileInputStream(original);
                 FileOutputStream fos = new FileOutputStream(backup)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                fos.flush();
            }

            logger.fine("Created backup: " + backup.getName());
            return true;
        } catch (IOException e) {
            logger.warning("Failed to backup file: " + original.getName() + " - " + e.getMessage());
            return false;
        }
    }

    /**
     * Get file size safely
     */
    public static long getFileSize(File file) {
        if (file == null || !file.exists()) {
            return 0;
        }
        return file.length();
    }

    /**
     * Check if file is readable
     */
    public static boolean isFileReadable(File file) {
        return file != null && file.exists() && file.isFile() && file.canRead();
    }

    /**
     * Check if file is writable (or can be created)
     */
    public static boolean isFileWritable(File file) {
        if (file == null) {
            return false;
        }

        if (file.exists()) {
            return file.isFile() && file.canWrite();
        }

        // Check if parent directory is writable
        File parent = file.getParentFile();
        return parent != null && parent.exists() && parent.isDirectory() && parent.canWrite();
    }
}
