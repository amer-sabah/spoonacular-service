package com.wiley.spoonacular.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spoonacular.client.ApiClient;
import com.spoonacular.client.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Utility class for configuring Spoonacular API clients with custom Gson settings.
 */
public class ApiClientConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(ApiClientConfigurer.class);

    private ApiClientConfigurer() {
        // Private constructor to prevent instantiation
    }

    /**
     * Configure an ApiClient with custom Gson settings to be lenient and ignore unknown fields.
     * 
     * @param apiClient The ApiClient to configure
     */
    public static void configureGson(ApiClient apiClient) {
        try {
            JSON json = apiClient.getJSON();
            Gson customGson = new GsonBuilder()
                    .setLenient()
                    .create();
            
            // Use reflection to replace the Gson instance in JSON object
            Field gsonField = JSON.class.getDeclaredField("gson");
            gsonField.setAccessible(true);
            gsonField.set(json, customGson);
        } catch (Exception e) {
            // If reflection fails, log warning but continue with default configuration
            logger.warn("Could not configure custom Gson: {}", e.getMessage(), e);
        }
    }
}
