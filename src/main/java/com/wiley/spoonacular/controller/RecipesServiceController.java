package com.wiley.spoonacular.controller;

import com.spoonacular.client.ApiException;
import com.spoonacular.client.model.SearchRecipes200Response;
import com.wiley.spoonacular.service.SpoonacularApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipes")
public class RecipesServiceController {

    private final SpoonacularApiService spoonacularApiService;

    public RecipesServiceController(SpoonacularApiService spoonacularApiService) {
        this.spoonacularApiService = spoonacularApiService;
    }

    /**
     * Search for recipes by query.
     * Example: GET /api/recipes/search?query=pasta&number=5
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
            SearchRecipes200Response response = spoonacularApiService.searchRecipes(query, maxResultSize);
            return ResponseEntity.ok(response);
        } catch (ApiException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error searching recipes: " + e.getMessage());
        }
    }
}
