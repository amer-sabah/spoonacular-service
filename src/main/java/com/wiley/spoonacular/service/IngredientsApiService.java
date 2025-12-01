package com.wiley.spoonacular.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spoonacular.IngredientsApi;
import com.spoonacular.client.ApiClient;
import com.spoonacular.client.ApiException;
import com.spoonacular.client.JSON;
import com.spoonacular.client.model.IngredientInformation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * Service for ingredient operations using the Spoonacular API SDK.
 */
@Service
public class IngredientsApiService {

    @Value("${spoonacular.api.key}")
    private String apiKey;

    private IngredientsApi ingredientsApi;

    /**
     * Initialize the Spoonacular API client with custom Gson configuration
     * to ignore unknown fields from API responses.
     */
    private IngredientsApi getIngredientsApi() {
        if (ingredientsApi == null) {
            ApiClient apiClient = new ApiClient();
            apiClient.setApiKey(apiKey);
            
            // Configure Gson to be lenient and ignore unknown fields
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
                System.err.println("Warning: Could not configure custom Gson: " + e.getMessage());
            }
            
            ingredientsApi = new IngredientsApi(apiClient);
        }
        return ingredientsApi;
    }

    /**
     * Get detailed information about a specific ingredient.
     * 
     * @param id The ingredient ID
     * @param amount The amount of the ingredient (optional)
     * @param unit The unit of the ingredient amount (optional)
     * @return Detailed ingredient information
     * @throws ApiException if the API call fails
     */
    public IngredientInformation getIngredientInformation(Integer id, BigDecimal amount, String unit) throws ApiException {
        return getIngredientsApi().getIngredientInformation(
                id,
                amount,
                unit
        );
    }
}
