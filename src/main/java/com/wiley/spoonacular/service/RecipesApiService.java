package com.wiley.spoonacular.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.spoonacular.RecipesApi;
import com.spoonacular.client.ApiClient;
import com.spoonacular.client.ApiException;
import com.spoonacular.client.JSON;
import com.spoonacular.client.model.RecipeInformation;
import com.spoonacular.client.model.SearchRecipes200Response;
import com.wiley.spoonacular.cache.JsonFileCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

/**
 * Service for searching recipes using the Spoonacular API SDK.
 */
@Service
public class RecipesApiService {

    private static final Logger logger = LoggerFactory.getLogger(RecipesApiService.class);

    @Value("${spoonacular.api.key}")
    private String apiKey;

    private RecipesApi recipesApi;
    private JsonFileCache<SearchRecipes200Response> searchCache;
    private JsonFileCache<RecipeInformation> recipeInfoCache;

    /**
     * Initialize caches on first access.
     */
    private void initializeCaches() {
        if (searchCache == null) {
            searchCache = new JsonFileCache<>("recipes/search", 
                new TypeToken<SearchRecipes200Response>(){}.getType(), 24, 100);
        }
        if (recipeInfoCache == null) {
            recipeInfoCache = new JsonFileCache<>("recipes/info", 
                new TypeToken<RecipeInformation>(){}.getType(), 24, 100);
        }
    }

    /**
     * Initialize the Spoonacular API client with custom Gson configuration
     * to ignore unknown fields from API responses.
     */
    private RecipesApi getRecipesApi() {
        if (recipesApi == null) {
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
                logger.warn("Could not configure custom Gson: {}", e.getMessage(), e);
            }
            
            recipesApi = new RecipesApi(apiClient);
        }
        return recipesApi;
    }

    /**
     * Search for recipes by query string.
     * 
     * @param query The search query (e.g., "pasta", "chicken")
     * @param maxResultSize Maximum number of results to return (default 10)
     * @param cuisines Optional list of cuisines to filter by (e.g., ["Italian", "Mexican"])
     * @param maxCalories Optional maximum calories per serving
     * @return Search results containing recipe information
     * @throws ApiException if the API call fails
     */
    public SearchRecipes200Response searchRecipes(String query, Integer maxResultSize, java.util.List<String> cuisines, Integer maxCalories) throws ApiException {
        final Integer resultSize = (maxResultSize == null) ? 10 : maxResultSize;
        
        // Initialize caches if needed
        initializeCaches();
        
        // Convert cuisines list to comma-separated string for API
        String cuisineString = (cuisines != null && !cuisines.isEmpty()) 
            ? String.join(",", cuisines) 
            : null;
        
        // Convert maxCalories to BigDecimal for API
        java.math.BigDecimal maxCaloriesBD = (maxCalories != null) 
            ? java.math.BigDecimal.valueOf(maxCalories) 
            : null;
        
        // Check cache first
        String cacheKey = searchCache.generateCacheKey(query, resultSize, cuisineString, maxCalories);
        SearchRecipes200Response cachedResult = searchCache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // Search recipes with query and number of results
        // All other parameters set to null for simplicity
        SearchRecipes200Response response = getRecipesApi().searchRecipes(
                query,          // query - what to search for
                cuisineString,  // cuisine - comma-separated list of cuisines
                null,       // excludeCuisine
                null,       // diet
                null,       // intolerances
                null,       // equipment
                null,       // includeIngredients
                null,       // excludeIngredients
                null,       // type
                null,       // instructionsRequired
                null,       // fillIngredients
                null,       // addRecipeInformation
                null,       // addRecipeNutrition
                null,       // author
                null,       // tags
                null,       // recipeBoxId
                null,       // titleMatch
                null,       // maxReadyTime
                null,       // minServings
                null,       // maxServings
                null,       // ignorePantry
                null,       // sort
                null,       // sortDirection
                null,       // minCarbs
                null,       // maxCarbs
                null,       // minProtein
                null,       // maxProtein
                null,       // minCalories
                maxCaloriesBD,       // maxCalories
                null,       // minFat
                null,       // maxFat
                null,       // minAlcohol
                null,       // maxAlcohol
                null,       // minCaffeine
                null,       // maxCaffeine
                null,       // minCopper
                null,       // maxCopper
                null,       // minCalcium
                null,       // maxCalcium
                null,       // minCholine
                null,       // maxCholine
                null,       // minCholesterol
                null,       // maxCholesterol
                null,       // minFluoride
                null,       // maxFluoride
                null,       // minSaturatedFat
                null,       // maxSaturatedFat
                null,       // minVitaminA
                null,       // maxVitaminA
                null,       // minVitaminC
                null,       // maxVitaminC
                null,       // minVitaminD
                null,       // maxVitaminD
                null,       // minVitaminE
                null,       // maxVitaminE
                null,       // minVitaminK
                null,       // maxVitaminK
                null,       // minVitaminB1
                null,       // maxVitaminB1
                null,       // minVitaminB2
                null,       // maxVitaminB2
                null,       // minVitaminB5
                null,       // maxVitaminB5
                null,       // minVitaminB3
                null,       // maxVitaminB3
                null,       // minVitaminB6
                null,       // maxVitaminB6
                null,       // minVitaminB12
                null,       // maxVitaminB12
                null,       // minFiber
                null,       // maxFiber
                null,       // minFolate
                null,       // maxFolate
                null,       // minFolicAcid
                null,       // maxFolicAcid
                null,       // minIodine
                null,       // maxIodine
                null,       // minIron
                null,       // maxIron
                null,       // minMagnesium
                null,       // maxMagnesium
                null,       // minManganese
                null,       // maxManganese
                null,       // minPhosphorus
                null,       // maxPhosphorus
                null,       // minPotassium
                null,       // maxPotassium
                null,       // minSelenium
                null,       // maxSelenium
                null,       // minSodium
                null,       // maxSodium
                null,       // minSugar
                null,       // maxSugar
                null,       // minZinc
                null,       // maxZinc
                0,          // offset
                resultSize      // number of results
        );
        
        // Cache the response
        searchCache.put(cacheKey, response);
        return response;
    }

    /**
     * Get detailed information about a specific recipe.
     * 
     * @param id The recipe ID
     * @param includeNutrition Whether to include nutrition data (default false)
     * @return Detailed recipe information
     * @throws ApiException if the API call fails
     */
    public RecipeInformation getRecipeInformation(Integer id, Boolean includeNutrition) throws ApiException {
        final Boolean includeNutritionData = (includeNutrition == null) ? false : includeNutrition;
        
        // Initialize caches if needed
        initializeCaches();
        
        // Check cache first
        String cacheKey = recipeInfoCache.generateCacheKey(id, includeNutritionData);
        RecipeInformation cachedResult = recipeInfoCache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        RecipeInformation response = getRecipesApi().getRecipeInformation(
                id,
                includeNutritionData,
                null,  // addWinePairing
                null   // addTasteData
        );
        
        // Cache the response
        recipeInfoCache.put(cacheKey, response);
        return response;
    }
}
