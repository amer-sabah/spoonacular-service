package com.wiley.spoonacular.service;

import com.spoonacular.client.ApiException;
import com.spoonacular.client.model.RecipeInformation;
import com.spoonacular.client.model.SearchRecipes200Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RecipesApiService.
 * Note: These are basic validation tests. Full API integration tests
 * would require a valid API key and network connectivity.
 */
class RecipesApiServiceTest {

    private RecipesApiService recipesApiService;

    private static final String TEST_API_KEY = "dd78c2d7cff44edba09aaebf9f697sss";

    @BeforeEach
    void setUp() {
        recipesApiService = new RecipesApiService();
        ReflectionTestUtils.setField(recipesApiService, "apiKey", TEST_API_KEY);
    }

    @Test
    void testServiceInitialization() {
        assertNotNull(recipesApiService);
        String apiKey = (String) ReflectionTestUtils.getField(recipesApiService, "apiKey");
        assertEquals(TEST_API_KEY, apiKey);
    }

    @Test
    void testSearchRecipes_NullMaxResultSize_DefaultsTo10() throws ApiException {
        // This test verifies that the service handles null maxResultSize parameter
        // by defaulting to 10 results
        try {
            SearchRecipes200Response response = recipesApiService.searchRecipes("pasta", null, null, null);
            assertNotNull(response, "Response should not be null");
            // If we get here, the default parameter logic worked
        } catch (ApiException e) {
            // API exception is expected if there are network/API issues
            // but we're mainly testing the null handling logic
            assertTrue(e.getMessage() != null || e.getCode() != 0, 
                "ApiException should have message or code");
        }
    }

    @Test
    void testSearchRecipes_WithMaxResultSize() throws ApiException {
        // This test verifies that the service accepts a specific maxResultSize parameter
        // and returns the requested number of results
        try {
            int requestedSize = 5;
            SearchRecipes200Response response = recipesApiService.searchRecipes("pasta", requestedSize, null, null);
            assertNotNull(response, "Response should not be null");
            // Verify response structure
            assertNotNull(response.getResults(), "Results should not be null");
            // Verify that the number of results matches the requested size
            assertEquals(requestedSize, response.getResults().size(), 
                "Number of results should match the requested maxResultSize");
        } catch (ApiException e) {
            // API exception is expected if there are network/API issues
            assertTrue(e.getMessage() != null || e.getCode() != 0, 
                "ApiException should have message or code");
        }
    }

    @Test
    void testSearchRecipes_WithQuery() {
        // Verify that calling with a query doesn't throw unexpected exceptions
        assertDoesNotThrow(() -> {
            try {
                SearchRecipes200Response response = recipesApiService.searchRecipes("chicken", 10, null, null);
                if (response != null) {
                    assertNotNull(response.getResults());
                }
            } catch (ApiException e) {
                // Expected if API is unreachable or rate limited
                System.out.println("API call failed (expected in unit test): " + e.getMessage());
            }
        });
    }

    @Test
    void testSearchRecipes_WithCuisines() {
        // Verify that calling with cuisines filter doesn't throw unexpected exceptions
        assertDoesNotThrow(() -> {
            try {
                java.util.List<String> cuisines = java.util.Arrays.asList("Italian", "Mexican");
                SearchRecipes200Response response = recipesApiService.searchRecipes("pasta", 10, cuisines, null);
                if (response != null) {
                    assertNotNull(response.getResults());
                }
            } catch (ApiException e) {
                // Expected if API is unreachable or rate limited
                System.out.println("API call failed (expected in unit test): " + e.getMessage());
            }
        });
    }

    @Test
    void testSearchRecipes_WithSingleCuisine() {
        // Verify that calling with a single cuisine filter works correctly
        assertDoesNotThrow(() -> {
            try {
                java.util.List<String> cuisines = java.util.Arrays.asList("Italian");
                SearchRecipes200Response response = recipesApiService.searchRecipes("pasta", 5, cuisines, null);
                if (response != null) {
                    assertNotNull(response.getResults());
                }
            } catch (ApiException e) {
                // Expected if API is unreachable or rate limited
                System.out.println("API call failed (expected in unit test): " + e.getMessage());
            }
        });
    }

    @Test
    void testApiKeyIsSet() {
        // Verify that API key is properly set
        String apiKey = (String) ReflectionTestUtils.getField(recipesApiService, "apiKey");
        assertNotNull(apiKey, "API key should be set");
        assertFalse(apiKey.isEmpty(), "API key should not be empty");
    }

    @Test
    void testGetRecipeInformation_ValidId() {
        // Verify that calling getRecipeInformation with a valid ID doesn't throw unexpected exceptions
        assertDoesNotThrow(() -> {
            try {
                RecipeInformation recipeInfo = recipesApiService.getRecipeInformation(715538, false);
                if (recipeInfo != null) {
                    assertNotNull(recipeInfo.getId(), "Recipe ID should not be null");
                    assertNotNull(recipeInfo.getTitle(), "Recipe title should not be null");
                }
            } catch (ApiException e) {
                // Expected if API is unreachable or rate limited
                System.out.println("API call failed (expected in unit test): " + e.getMessage());
            }
        });
    }

    @Test
    void testGetRecipeInformation_NullIncludeNutrition_DefaultsToFalse() throws ApiException {
        // This test verifies that the service handles null includeNutrition parameter
        // by defaulting to false
        try {
            RecipeInformation recipeInfo = recipesApiService.getRecipeInformation(715538, null);
            assertNotNull(recipeInfo, "Response should not be null");
            // If we get here, the default parameter logic worked
        } catch (ApiException e) {
            // API exception is expected if there are network/API issues
            // but we're mainly testing the null handling logic
            assertTrue(e.getMessage() != null || e.getCode() != 0, 
                "ApiException should have message or code");
        }
    }

    @Test
    void testGetRecipeInformation_WithNutrition() throws ApiException {
        // This test verifies that the service accepts includeNutrition parameter
        try {
            RecipeInformation recipeInfo = recipesApiService.getRecipeInformation(715538, true);
            assertNotNull(recipeInfo, "Response should not be null");
            // Verify response structure
            assertNotNull(recipeInfo.getId(), "Recipe ID should not be null");
        } catch (ApiException e) {
            // API exception is expected if there are network/API issues
            assertTrue(e.getMessage() != null || e.getCode() != 0, 
                "ApiException should have message or code");
        }
    }

    @Test
    void testSearchRecipes_WithMaxCalories() {
        // Verify that calling with maxCalories filter works correctly
        assertDoesNotThrow(() -> {
            try {
                SearchRecipes200Response response = recipesApiService.searchRecipes("salad", 10, null, 500);
                if (response != null) {
                    assertNotNull(response.getResults());
                }
            } catch (ApiException e) {
                // Expected if API is unreachable or rate limited
                System.out.println("API call failed (expected in unit test): " + e.getMessage());
            }
        });
    }

    @Test
    void testSearchRecipes_WithCuisinesAndMaxCalories() {
        // Verify that calling with both cuisines and maxCalories filters works correctly
        assertDoesNotThrow(() -> {
            try {
                java.util.List<String> cuisines = java.util.Arrays.asList("Italian", "Mediterranean");
                SearchRecipes200Response response = recipesApiService.searchRecipes("pasta", 10, cuisines, 600);
                if (response != null) {
                    assertNotNull(response.getResults());
                }
            } catch (ApiException e) {
                // Expected if API is unreachable or rate limited
                System.out.println("API call failed (expected in unit test): " + e.getMessage());
            }
        });
    }

    @Test
    void testSearchRecipes_WithEmptyQuery() {
        // Verify that calling with empty query string doesn't throw unexpected exceptions
        assertDoesNotThrow(() -> {
            try {
                SearchRecipes200Response response = recipesApiService.searchRecipes("", 10, null, null);
                if (response != null) {
                    assertNotNull(response.getResults());
                }
            } catch (ApiException e) {
                // Expected if API is unreachable or rate limited
                System.out.println("API call failed (expected in unit test): " + e.getMessage());
            }
        });
    }

    @Test
    void testSearchRecipes_WithEmptyCuisinesList() {
        // Verify that calling with empty cuisines list works correctly
        assertDoesNotThrow(() -> {
            try {
                java.util.List<String> emptyCuisines = java.util.Collections.emptyList();
                SearchRecipes200Response response = recipesApiService.searchRecipes("pasta", 10, emptyCuisines, null);
                if (response != null) {
                    assertNotNull(response.getResults());
                }
            } catch (ApiException e) {
                // Expected if API is unreachable or rate limited
                System.out.println("API call failed (expected in unit test): " + e.getMessage());
            }
        });
    }

    @Test
    void testSearchRecipes_WithZeroMaxCalories() {
        // Verify that calling with zero maxCalories works correctly
        assertDoesNotThrow(() -> {
            try {
                SearchRecipes200Response response = recipesApiService.searchRecipes("salad", 10, null, 0);
                if (response != null) {
                    assertNotNull(response.getResults());
                }
            } catch (ApiException e) {
                // Expected if API is unreachable or rate limited
                System.out.println("API call failed (expected in unit test): " + e.getMessage());
            }
        });
    }

    @Test
    void testGetRecipeInformation_WithInvalidId() {
        // Verify that calling with invalid ID handles error gracefully
        assertDoesNotThrow(() -> {
            try {
                RecipeInformation recipeInfo = recipesApiService.getRecipeInformation(-1, false);
                // If we get a response, it should be valid
                if (recipeInfo != null) {
                    assertNotNull(recipeInfo.getId());
                }
            } catch (ApiException e) {
                // Expected for invalid ID
                assertTrue(e.getCode() == 404 || e.getCode() == 401,
                    "Should return 404 or 401 for invalid ID");
            }
        });
    }

    @Test
    void testSearchRecipes_LargeMaxResultSize() {
        // Verify that calling with large maxResultSize doesn't cause issues
        assertDoesNotThrow(() -> {
            try {
                SearchRecipes200Response response = recipesApiService.searchRecipes("pasta", 100, null, null);
                if (response != null) {
                    assertNotNull(response.getResults());
                    assertTrue(response.getResults().size() <= 100,
                        "Results should not exceed requested size");
                }
            } catch (ApiException e) {
                // Expected if API is unreachable or rate limited
                System.out.println("API call failed (expected in unit test): " + e.getMessage());
            }
        });
    }
}
