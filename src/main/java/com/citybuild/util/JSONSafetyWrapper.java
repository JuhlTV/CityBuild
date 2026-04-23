package com.citybuild.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Safe JSON parsing wrapper to prevent NPE and type errors
 */
public class JSONSafetyWrapper {
    
    private final JsonObject json;
    
    public JSONSafetyWrapper(JsonObject json) {
        this.json = json != null ? json : new JsonObject();
    }
    
    /**
     * Safely get string with fallback
     */
    public String getString(String key, String fallback) {
        try {
            if (!json.has(key)) return fallback;
            JsonElement element = json.get(key);
            if (element == null || element.isJsonNull()) return fallback;
            return element.getAsString();
        } catch (Exception e) {
            return fallback;
        }
    }
    
    /**
     * Safely get integer with fallback
     */
    public int getInt(String key, int fallback) {
        try {
            if (!json.has(key)) return fallback;
            JsonElement element = json.get(key);
            if (element == null || element.isJsonNull()) return fallback;
            return element.getAsInt();
        } catch (Exception e) {
            return fallback;
        }
    }
    
    /**
     * Safely get double with fallback
     */
    public double getDouble(String key, double fallback) {
        try {
            if (!json.has(key)) return fallback;
            JsonElement element = json.get(key);
            if (element == null || element.isJsonNull()) return fallback;
            return element.getAsDouble();
        } catch (Exception e) {
            return fallback;
        }
    }
    
    /**
     * Safely get boolean with fallback
     */
    public boolean getBoolean(String key, boolean fallback) {
        try {
            if (!json.has(key)) return fallback;
            JsonElement element = json.get(key);
            if (element == null || element.isJsonNull()) return fallback;
            return element.getAsBoolean();
        } catch (Exception e) {
            return fallback;
        }
    }
    
    /**
     * Safely get JSON object with fallback
     */
    public JsonObject getObject(String key) {
        try {
            if (!json.has(key)) return new JsonObject();
            JsonElement element = json.get(key);
            if (element == null || element.isJsonNull()) return new JsonObject();
            return element.getAsJsonObject();
        } catch (Exception e) {
            return new JsonObject();
        }
    }
    
    /**
     * Safely get JSON array with fallback
     */
    public com.google.gson.JsonArray getArray(String key) {
        try {
            if (!json.has(key)) return new com.google.gson.JsonArray();
            JsonElement element = json.get(key);
            if (element == null || element.isJsonNull()) return new com.google.gson.JsonArray();
            return element.getAsJsonArray();
        } catch (Exception e) {
            return new com.google.gson.JsonArray();
        }
    }
    
    /**
     * Check if key exists and is not null
     */
    public boolean hasKey(String key) {
        return json.has(key) && !json.get(key).isJsonNull();
    }
    
    /**
     * Get underlying JSON object
     */
    public JsonObject getJson() {
        return json;
    }
}
