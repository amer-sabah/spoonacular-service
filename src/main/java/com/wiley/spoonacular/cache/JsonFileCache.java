package com.wiley.spoonacular.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * File-based JSON cache manager with TTL and size limits.
 */
public class JsonFileCache<T> {
    private static final Logger logger = LoggerFactory.getLogger(JsonFileCache.class);
    private static final long DEFAULT_TTL_HOURS = 24;
    private static final int DEFAULT_MAX_ENTRIES = 100;
    
    private final String cacheDir;
    private final long ttlMillis;
    private final int maxEntries;
    private final Gson gson;
    private final Type cacheEntryType;

    /**
     * Create a new JSON file cache.
     * 
     * @param cacheSubDir The subdirectory under ./cache/ for this cache
     * @param dataType The type of data being cached
     */
    public JsonFileCache(String cacheSubDir, Type dataType) {
        this(cacheSubDir, dataType, DEFAULT_TTL_HOURS, DEFAULT_MAX_ENTRIES);
    }

    /**
     * Create a new JSON file cache with custom TTL and size limit.
     * 
     * @param cacheSubDir The subdirectory under ./cache/ for this cache
     * @param dataType The type of data being cached
     * @param ttlHours Time to live in hours
     * @param maxEntries Maximum number of cache entries
     */
    public JsonFileCache(String cacheSubDir, Type dataType, long ttlHours, int maxEntries) {
        this.cacheDir = "./cache/" + cacheSubDir;
        this.ttlMillis = ttlHours * 60 * 60 * 1000;
        this.maxEntries = maxEntries;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.cacheEntryType = TypeToken.getParameterized(CacheEntry.class, dataType).getType();
        
        // Create cache directory if it doesn't exist
        createCacheDirectory();
    }

    /**
     * Create the cache directory structure.
     */
    private void createCacheDirectory() {
        try {
            Files.createDirectories(Paths.get(cacheDir));
        } catch (IOException e) {
            logger.warn("Could not create cache directory: {}", e.getMessage(), e);
        }
    }

    /**
     * Generate a cache key from parameters.
     * 
     * @param params Variable parameters to create the key
     * @return MD5 hash of the parameters
     */
    public String generateCacheKey(Object... params) {
        StringBuilder sb = new StringBuilder();
        for (Object param : params) {
            sb.append(param != null ? param.toString() : "null").append("_");
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(sb.toString().getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple hash if MD5 is not available
            return String.valueOf(sb.toString().hashCode());
        }
    }

    /**
     * Get cached data if it exists and is not expired.
     * 
     * @param cacheKey The cache key
     * @return The cached data, or null if not found or expired
     */
    public T get(String cacheKey) {
        File cacheFile = new File(cacheDir, cacheKey + ".json");
        
        if (!cacheFile.exists()) {
            return null;
        }

        try {
            String json = Files.readString(cacheFile.toPath());
            CacheEntry<T> entry = gson.fromJson(json, cacheEntryType);
            
            if (entry == null) {
                return null;
            }

            // Check if expired
            if (entry.isExpired(ttlMillis)) {
                // Delete expired cache file
                cacheFile.delete();
                return null;
            }

            return entry.getData();
        } catch (IOException e) {
            logger.warn("Could not read cache file: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Store data in cache.
     * 
     * @param cacheKey The cache key
     * @param data The data to cache
     */
    public void put(String cacheKey, T data) {
        try {
            // Check and enforce max entries limit
            enforceMaxEntries();

            CacheEntry<T> entry = new CacheEntry<>(System.currentTimeMillis(), data);
            String json = gson.toJson(entry);
            
            File cacheFile = new File(cacheDir, cacheKey + ".json");
            Files.writeString(cacheFile.toPath(), json);
        } catch (IOException e) {
            logger.warn("Could not write cache file: {}", e.getMessage(), e);
        }
    }

    /**
     * Enforce the maximum number of cache entries by deleting oldest files.
     */
    private void enforceMaxEntries() {
        try {
            Path cachePath = Paths.get(cacheDir);
            if (!Files.exists(cachePath)) {
                return;
            }

            try (Stream<Path> files = Files.list(cachePath)) {
                Map<Path, Long> fileTimestamps = new LinkedHashMap<>();
                
                files.filter(Files::isRegularFile)
                     .filter(p -> p.toString().endsWith(".json"))
                     .forEach(path -> {
                         try {
                             fileTimestamps.put(path, Files.getLastModifiedTime(path).toMillis());
                         } catch (IOException e) {
                             // Skip files that can't be read
                         }
                     });

                // If we're at or over the limit, delete oldest files
                int toDelete = fileTimestamps.size() - maxEntries + 1; // +1 for the new entry we're about to add
                if (toDelete > 0) {
                    deleteOldestFiles(fileTimestamps, toDelete);
                }
            }
        } catch (IOException e) {
            logger.warn("Could not enforce cache size limit: {}", e.getMessage(), e);
        }
    }

    /**
     * Delete the oldest cache files based on their timestamps.
     * 
     * @param fileTimestamps Map of file paths to their last modified timestamps
     * @param count Number of files to delete
     */
    private void deleteOldestFiles(Map<Path, Long> fileTimestamps, int count) {
        fileTimestamps.entrySet().stream()
            .sorted(Comparator.comparingLong(Map.Entry::getValue))
            .limit(count)
            .forEach(entry -> {
                try {
                    Files.delete(entry.getKey());
                } catch (IOException e) {
                    logger.warn("Could not delete old cache file: {}", e.getMessage(), e);
                }
            });
    }

    /**
     * Clear all cache entries.
     */
    public void clear() {
        try {
            Path cachePath = Paths.get(cacheDir);
            if (Files.exists(cachePath)) {
                try (Stream<Path> files = Files.list(cachePath)) {
                    files.filter(Files::isRegularFile)
                         .filter(p -> p.toString().endsWith(".json"))
                         .forEach(path -> {
                             try {
                                 Files.delete(path);
                             } catch (IOException e) {
                                 logger.warn("Could not delete cache file: {}", e.getMessage(), e);
                             }
                         });
                }
            }
        } catch (IOException e) {
            logger.warn("Could not clear cache: {}", e.getMessage(), e);
        }
    }
}
