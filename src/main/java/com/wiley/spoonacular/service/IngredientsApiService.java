package com.wiley.spoonacular.service;

import com.spoonacular.IngredientsApi;
import com.spoonacular.client.ApiClient;
import com.spoonacular.client.ApiException;
import com.spoonacular.client.model.IngredientInformation;
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

    /**
     * Initialize the Spoonacular API client.
     */
    private IngredientsApi getIngredientsApi() {
        if (ingredientsApi == null) {
            ApiClient apiClient = new ApiClient();
            apiClient.setApiKey(apiKey);
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
