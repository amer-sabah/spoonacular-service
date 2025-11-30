package com.wiley.spoonacular.controller;

import com.spoonacular.client.ApiException;
import com.spoonacular.client.model.RecipeInformation;
import com.spoonacular.client.model.SearchRecipes200Response;
import com.wiley.spoonacular.service.RecipesApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipes")
public class RecipesServiceController {

    private final RecipesApiService recipesApiService;

    public RecipesServiceController(RecipesApiService recipesApiService) {
        this.recipesApiService = recipesApiService;
    }

    /**
     * Search for recipes by query.
     * Example: GET /recipes/search?query=pasta&number=5
     * 
     * @param query Search term (e.g., "pasta", "chicken", "salad")
     * @param maxResultSize Maximum number of results to return (default: 10, max: 100)
     * @return JSON response with recipe search results
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchRecipes(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "10") Integer maxResultSize) {
        try {
            SearchRecipes200Response response = recipesApiService.searchRecipes(query, maxResultSize);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting recipe information: " + e.getMessage());
        }
    }
}
