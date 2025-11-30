package com.wiley.spoonacular.controller;

import com.spoonacular.client.ApiException;
import com.spoonacular.client.model.IngredientInformation;
import com.wiley.spoonacular.service.IngredientsApiService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/ingredients")
public class IngredientsServiceController {

    private final IngredientsApiService ingredientsApiService;

    public IngredientsServiceController(IngredientsApiService ingredientsApiService) {
        this.ingredientsApiService = ingredientsApiService;
    }

    /**
     * Get detailed information about a specific ingredient.
     * Example: GET /ingredients/{id}?amount=100&unit=grams
     * 
     * @param id The ingredient ID
     * @param amount The amount of the ingredient (optional)
     * @param unit The unit of the ingredient amount (optional, e.g., "grams", "cups")
     * @return JSON response with detailed ingredient information
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getIngredientInformation(
            @PathVariable Integer id,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false) String unit) {
        try {
            IngredientInformation ingredientInfo = ingredientsApiService.getIngredientInformation(id, amount, unit);
            return ResponseEntity.ok(ingredientInfo);
        } catch (ApiException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting ingredient information: " + e.getMessage());
        }
    }
}
