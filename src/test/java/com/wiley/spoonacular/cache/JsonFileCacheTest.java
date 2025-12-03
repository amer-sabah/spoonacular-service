package com.wiley.spoonacular.cache;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JsonFileCache.
 */
class JsonFileCacheTest {

    private JsonFileCache<String> cache;
    private final String testCacheDir = "cache/test";

    @BeforeEach
    void setUp() {
        cache = new JsonFileCache<>("test", new TypeToken<String>(){}.getType(), 24, 100);
    }

    @AfterEach
    void tearDown() {
        // Clean up cache files after tests
        cache.clear();
        
        // Remove test cache directory
        File cacheDir = new File(testCacheDir);
        if (cacheDir.exists()) {
            cacheDir.delete();
        }
    }

    @Test
    void testCachePutAndGet() {
        String cacheKey = cache.generateCacheKey("test", "param1", 123);
        String testData = "Test cached data";
        
        // Put data in cache
        cache.put(cacheKey, testData);
        
        // Retrieve data from cache
        String cachedData = cache.get(cacheKey);
        
        assertNotNull(cachedData);
        assertEquals(testData, cachedData);
    }

    @Test
    void testCacheMiss() {
        String cacheKey = cache.generateCacheKey("nonexistent", "key");
        
        String cachedData = cache.get(cacheKey);
        
        assertNull(cachedData);
    }

    @Test
    void testCacheKeyGeneration() {
        String key1 = cache.generateCacheKey("query", 10);
        String key2 = cache.generateCacheKey("query", 10);
        String key3 = cache.generateCacheKey("query", 20);
        
        // Same parameters should generate same key
        assertEquals(key1, key2);
        
        // Different parameters should generate different key
        assertNotEquals(key1, key3);
    }

    @Test
    void testCacheClear() {
        String cacheKey1 = cache.generateCacheKey("test1");
        String cacheKey2 = cache.generateCacheKey("test2");
        
        cache.put(cacheKey1, "Data 1");
        cache.put(cacheKey2, "Data 2");
        
        // Verify data is cached
        assertNotNull(cache.get(cacheKey1));
        assertNotNull(cache.get(cacheKey2));
        
        // Clear cache
        cache.clear();
        
        // Verify data is gone
        assertNull(cache.get(cacheKey1));
        assertNull(cache.get(cacheKey2));
    }

    @Test
    void testMaxEntriesEnforcement() throws InterruptedException {
        // Create a cache with max 5 entries
        JsonFileCache<String> smallCache = new JsonFileCache<>("test-small", 
            new TypeToken<String>(){}.getType(), 24, 5);
        
        try {
            // Add 7 entries (exceeds max of 5)
            for (int i = 0; i < 7; i++) {
                String key = smallCache.generateCacheKey("test", i);
                smallCache.put(key, "Data " + i);
                // Small delay to ensure different timestamps
                Thread.sleep(10);
            }
            
            // Count remaining cache files
            File cacheDir = new File("cache/test-small");
            if (cacheDir.exists()) {
                File[] files = cacheDir.listFiles((dir, name) -> name.endsWith(".json"));
                assertNotNull(files);
                // Should have exactly 5 files (max entries)
                assertTrue(files.length <= 5, "Cache should not exceed max entries");
            }
        } finally {
            smallCache.clear();
            File cacheDir = new File("cache/test-small");
            if (cacheDir.exists()) {
                cacheDir.delete();
            }
        }
    }

    @Test
    void testNullParametersInCacheKey() {
        String key1 = cache.generateCacheKey("query", null);
        String key2 = cache.generateCacheKey("query", null);
        
        assertEquals(key1, key2);
        
        cache.put(key1, "Test data with null param");
        assertEquals("Test data with null param", cache.get(key2));
    }

    @Test
    void testCacheExpiration() throws InterruptedException {
        // Create a cache with 1 second TTL
        JsonFileCache<String> shortLivedCache = new JsonFileCache<>("test-ttl",
                new TypeToken<String>(){}.getType(), 0, 100); // 0 hours = expired immediately
        
        try {
            String cacheKey = shortLivedCache.generateCacheKey("test", "expiration");
            shortLivedCache.put(cacheKey, "Expiring data");
            
            // Small delay to ensure expiration
            Thread.sleep(100);
            
            // Data should be expired and return null
            String cachedData = shortLivedCache.get(cacheKey);
            assertNull(cachedData, "Expired cache entry should return null");
        } finally {
            shortLivedCache.clear();
            File cacheDir = new File("cache/test-ttl");
            if (cacheDir.exists()) {
                cacheDir.delete();
            }
        }
    }

    @Test
    void testEmptyStringCacheKey() {
        String key = cache.generateCacheKey("");
        assertNotNull(key, "Cache key should be generated for empty string");
        assertFalse(key.isEmpty(), "Generated cache key should not be empty");
        
        cache.put(key, "Data for empty key");
        assertEquals("Data for empty key", cache.get(key));
    }

    @Test
    void testCacheKeyWithSpecialCharacters() {
        String key1 = cache.generateCacheKey("query with spaces", "param@#$%");
        String key2 = cache.generateCacheKey("query with spaces", "param@#$%");
        
        assertEquals(key1, key2, "Same parameters with special characters should generate same key");
        
        cache.put(key1, "Special chars data");
        assertEquals("Special chars data", cache.get(key2));
    }

    @Test
    void testMultiplePutsOverwritesPreviousData() {
        String cacheKey = cache.generateCacheKey("overwrite", "test");
        
        cache.put(cacheKey, "First data");
        assertEquals("First data", cache.get(cacheKey));
        
        cache.put(cacheKey, "Second data");
        assertEquals("Second data", cache.get(cacheKey), "Second put should overwrite first data");
    }
}
