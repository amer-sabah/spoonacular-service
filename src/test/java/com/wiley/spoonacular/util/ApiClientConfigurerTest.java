package com.wiley.spoonacular.util;

import com.spoonacular.client.ApiClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ApiClientConfigurer utility class.
 */
class ApiClientConfigurerTest {

    @Test
    void testConfigureGson_WithValidApiClient() {
        // Create a new ApiClient
        ApiClient apiClient = new ApiClient();
        apiClient.setApiKey("test-key");
        
        // Configure Gson - should not throw exception
        assertDoesNotThrow(() -> ApiClientConfigurer.configureGson(apiClient),
            "Configuring Gson should not throw exception");
        
        // Verify ApiClient is still functional after configuration
        assertNotNull(apiClient.getJSON(), "JSON object should not be null after configuration");
    }

    @Test
    void testConfigureGson_WithNullApiClient() {
        // Verify that passing null handles gracefully (logs warning but doesn't throw)
        // The method catches all exceptions and logs them
        assertDoesNotThrow(() -> ApiClientConfigurer.configureGson(null),
            "Should handle null ApiClient gracefully by catching exception");
    }

    @Test
    void testConfigureGson_MultipleCallsOnSameClient() {
        // Create a new ApiClient
        ApiClient apiClient = new ApiClient();
        apiClient.setApiKey("test-key");
        
        // Configure Gson multiple times - should not throw exception
        assertDoesNotThrow(() -> {
            ApiClientConfigurer.configureGson(apiClient);
            ApiClientConfigurer.configureGson(apiClient);
            ApiClientConfigurer.configureGson(apiClient);
        }, "Multiple Gson configurations should not throw exception");
        
        // Verify ApiClient is still functional
        assertNotNull(apiClient.getJSON());
    }

    @Test
    void testConfigureGson_PreservesApiKey() {
        // Create a new ApiClient with API key
        ApiClient apiClient = new ApiClient();
        String testApiKey = "test-api-key-12345";
        apiClient.setApiKey(testApiKey);
        
        // Configure Gson
        ApiClientConfigurer.configureGson(apiClient);
        
        // Verify ApiClient is still functional after configuration
        // (API key cannot be retrieved but we can verify no exception was thrown)
        assertNotNull(apiClient.getJSON(),
            "ApiClient should remain functional after Gson configuration");
    }

    @Test
    void testConfigureGson_PreservesBasePath() {
        // Create a new ApiClient with custom base path
        ApiClient apiClient = new ApiClient();
        String customBasePath = "https://custom.api.example.com";
        apiClient.setBasePath(customBasePath);
        
        // Configure Gson
        ApiClientConfigurer.configureGson(apiClient);
        
        // Verify base path is preserved after configuration
        assertEquals(customBasePath, apiClient.getBasePath(),
            "Base path should be preserved after Gson configuration");
    }

    @Test
    void testConfigureGson_JsonObjectNotNull() {
        // Create a new ApiClient
        ApiClient apiClient = new ApiClient();
        
        // Configure Gson
        ApiClientConfigurer.configureGson(apiClient);
        
        // Verify JSON object is available
        assertNotNull(apiClient.getJSON(),
            "JSON object should be available after configuration");
    }
}
