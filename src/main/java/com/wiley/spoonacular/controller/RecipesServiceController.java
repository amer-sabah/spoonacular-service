package com.wiley.spoonacular.controller;

import com.spoonacular.client.ApiException;
import com.spoonacular.client.model.RecipeInformation;
import com.spoonacular.client.model.SearchRecipes200Response;
import com.wiley.spoonacular.service.RecipesApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipes")
public class RecipesServiceController {

    private static final Logger logger = LoggerFactory.getLogger(RecipesServiceController.class);
    private final RecipesApiService recipesApiService;

    public RecipesServiceController(RecipesApiService recipesApiService) {
        this.recipesApiService = recipesApiService;
    }

    /**
     * Search for recipes by query.
     * Example: GET /recipes/search?query=pasta&number=5
     * Example with cuisines: GET /recipes/search?query=pasta&cuisines=Italian,Mexican
     * 
     * @param query Search term (e.g., "pasta", "chicken", "salad")
     * @param maxResultSize Maximum number of results to return (default: 10, max: 100)
     * @param cuisines Optional list of cuisines to filter by (e.g., Italian, Mexican, Chinese)
     * @param maxCalories Optional maximum calories per serving
     * @return JSON response with recipe search results
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchRecipes(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "12") Integer maxResultSize,
            @RequestParam(required = false) java.util.List<String> cuisines,
            @RequestParam(required = false) Integer maxCalories) {
        try {
            SearchRecipes200Response response = recipesApiService.searchRecipes(query, maxResultSize, cuisines, maxCalories);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            logger.error("Error searching recipes with query '{}': {}", query, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching recipes: " + e.getMessage());
        }
    }

    /**
     * Get detailed information about a specific recipe.
     * Example: GET /recipes/{id}?includeNutrition=true
     * 
     * @param id The recipe ID
     * @param includeNutrition Whether to include nutrition data (default: false)
     * @return JSON response with detailed recipe information
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeInformation(
            @PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "false") Boolean includeNutrition) {
        try {
            RecipeInformation recipeInfo = recipesApiService.getRecipeInformation(id, includeNutrition);
            return ResponseEntity.ok(recipeInfo);
        } catch (ApiException e) {
            logger.error("Error getting recipe information for id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting recipe information: " + e.getMessage());
        }
    }
}
