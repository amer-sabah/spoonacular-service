package com.wiley.spoonacular.service;

import com.google.gson.reflect.TypeToken;
import com.spoonacular.IngredientsApi;
import com.spoonacular.client.ApiClient;
import com.spoonacular.client.ApiException;
import com.spoonacular.client.model.IngredientInformation;
import com.wiley.spoonacular.cache.JsonFileCache;
import com.wiley.spoonacular.util.ApiClientConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service for ingredient operations using the Spoonacular API SDK.
 */
@Service
public class IngredientsApiService {

    @Value("${spoonacular.api.key}")
    private String apiKey;

    private IngredientsApi ingredientsApi;
    private JsonFileCache<IngredientInformation> ingredientInfoCache;

    /**
     * Initialize cache on first access.
     */
    private void initializeCache() {
        if (ingredientInfoCache == null) {
            ingredientInfoCache = new JsonFileCache<>("ingredients/info", 
                new TypeToken<IngredientInformation>(){}.getType(), 24, 100);
        }
    }

    /**
     * Initialize the Spoonacular API client with custom Gson configuration
     * to ignore unknown fields from API responses.
     */
    private IngredientsApi getIngredientsApi() {
        if (ingredientsApi == null) {
            ApiClient apiClient = new ApiClient();
            apiClient.setApiKey(apiKey);
            
            // Configure Gson to be lenient and ignore unknown fields
            ApiClientConfigurer.configureGson(apiClient);
            
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
        // Initialize cache if needed
        initializeCache();
        
        // Check cache first
        String cacheKey = ingredientInfoCache.generateCacheKey(id, amount, unit);
        IngredientInformation cachedResult = ingredientInfoCache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        IngredientInformation response = getIngredientsApi().getIngredientInformation(
                id,
                amount,
                unit
        );
        
        // Cache the response
        ingredientInfoCache.put(cacheKey, response);
        return response;
    }
}
