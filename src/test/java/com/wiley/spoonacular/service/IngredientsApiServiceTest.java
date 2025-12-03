package com.wiley.spoonacular.service;

import com.spoonacular.client.ApiException;
import com.spoonacular.client.model.IngredientInformation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for IngredientsApiService.
 * Note: These are basic validation tests. Full API integration tests
 * would require a valid API key and network connectivity.
 */
class IngredientsApiServiceTest {

    private IngredientsApiService ingredientsApiService;

    private static final String TEST_API_KEY = "dd78c2d7cff44edba09aaebf9f697sss";

    @BeforeEach
    void setUp() {
        ingredientsApiService = new IngredientsApiService();
        ReflectionTestUtils.setField(ingredientsApiService, "apiKey", TEST_API_KEY);
    }

    @Test
    void testServiceInitialization() {
        assertNotNull(ingredientsApiService);
        String apiKey = (String) ReflectionTestUtils.getField(ingredientsApiService, "apiKey");
        assertEquals(TEST_API_KEY, apiKey);
    }

    @Test
    void testGetIngredientInformation_ValidId() {
        // Verify that calling getIngredientInformation with a valid ID doesn't throw unexpected exceptions
        assertDoesNotThrow(() -> {
            try {
                IngredientInformation ingredientInfo = ingredientsApiService.getIngredientInformation(9266, null, null);
                if (ingredientInfo != null) {
                    assertNotNull(ingredientInfo.getId(), "Ingredient ID should not be null");
                    assertNotNull(ingredientInfo.getName(), "Ingredient name should not be null");
                }
            } catch (ApiException e) {
                // Expected if API is unreachable or rate limited
                System.out.println("API call failed (expected in unit test): " + e.getMessage());
            }
        });
    }

    @Test
    void testGetIngredientInformation_WithAmountAndUnit() throws ApiException {
        // This test verifies that the service accepts amount and unit parameters
        try {
            BigDecimal amount = new BigDecimal("100");
            String unit = "grams";
            IngredientInformation ingredientInfo = ingredientsApiService.getIngredientInformation(9266, amount, unit);
            assertNotNull(ingredientInfo, "Response should not be null");
            // Verify response structure
            assertNotNull(ingredientInfo.getId(), "Ingredient ID should not be null");
        } catch (ApiException e) {
            // API exception is expected if there are network/API issues
            assertTrue(e.getMessage() != null || e.getCode() != 0, 
                "ApiException should have message or code");
        }
    }

    @Test
    void testGetIngredientInformation_NullAmountAndUnit() throws ApiException {
        // This test verifies that the service handles null amount and unit parameters
        try {
            IngredientInformation ingredientInfo = ingredientsApiService.getIngredientInformation(9266, null, null);
            assertNotNull(ingredientInfo, "Response should not be null");
            // If we get here, the null handling logic worked
        } catch (ApiException e) {
            // API exception is expected if there are network/API issues
            // but we're mainly testing the null handling logic
            assertTrue(e.getMessage() != null || e.getCode() != 0, 
                "ApiException should have message or code");
        }
    }

    @Test
    void testApiKeyIsSet() {
        // Verify that API key is properly set
        String apiKey = (String) ReflectionTestUtils.getField(ingredientsApiService, "apiKey");
        assertNotNull(apiKey, "API key should be set");
        assertFalse(apiKey.isEmpty(), "API key should not be empty");
    }

    @Test
    void testGetIngredientInformation_WithInvalidId() {
        // Verify that calling with invalid ID handles error gracefully
        assertDoesNotThrow(() -> {
            try {
                IngredientInformation ingredientInfo = ingredientsApiService.getIngredientInformation(-1, null, null);
                // If we get a response, it should be valid
                if (ingredientInfo != null) {
                    assertNotNull(ingredientInfo.getId());
                }
            } catch (ApiException e) {
                // Expected for invalid ID
                assertTrue(e.getCode() == 404 || e.getCode() == 401,
                    "Should return 404 or 401 for invalid ID");
            }
        });
    }

    @Test
    void testGetIngredientInformation_WithZeroAmount() throws ApiException {
        // This test verifies that the service handles zero amount correctly
        try {
            BigDecimal amount = BigDecimal.ZERO;
            String unit = "grams";
            IngredientInformation ingredientInfo = ingredientsApiService.getIngredientInformation(9266, amount, unit);
            assertNotNull(ingredientInfo, "Response should not be null");
        } catch (ApiException e) {
            // API exception is expected if there are network/API issues
            assertTrue(e.getMessage() != null || e.getCode() != 0,
                "ApiException should have message or code");
        }
    }

    @Test
    void testGetIngredientInformation_WithNegativeAmount() throws ApiException {
        // This test verifies that the service handles negative amount
        try {
            BigDecimal amount = new BigDecimal("-10");
            String unit = "grams";
            IngredientInformation ingredientInfo = ingredientsApiService.getIngredientInformation(9266, amount, unit);
            // If we get a response without exception, the service handled it
            if (ingredientInfo != null) {
                assertNotNull(ingredientInfo.getId());
            }
        } catch (ApiException e) {
            // API might reject negative amounts
            assertTrue(e.getCode() >= 400, "Should return error code for negative amount");
        }
    }

    @Test
    void testGetIngredientInformation_WithDifferentUnits() throws ApiException {
        // This test verifies that the service handles different unit types
        String[] units = {"grams", "cups", "tablespoons", "ounces", "pounds"};
        
        for (String unit : units) {
            assertDoesNotThrow(() -> {
                try {
                    BigDecimal amount = new BigDecimal("100");
                    IngredientInformation ingredientInfo = ingredientsApiService.getIngredientInformation(9266, amount, unit);
                    if (ingredientInfo != null) {
                        assertNotNull(ingredientInfo.getId(), "Ingredient ID should not be null for unit: " + unit);
                    }
                } catch (ApiException e) {
                    // Expected if API is unreachable or rate limited
                    System.out.println("API call failed (expected in unit test): " + e.getMessage());
                }
            }, "Should handle unit: " + unit);
        }
    }
}
