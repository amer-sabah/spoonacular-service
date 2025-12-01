package com.wiley.spoonacular.cache;

/**
 * Represents a cache entry with timestamp and cached data.
 */
public class CacheEntry<T> {
    private long timestamp;
    private T data;

    public CacheEntry() {
    }

    public CacheEntry(long timestamp, T data) {
        this.timestamp = timestamp;
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * Check if the cache entry is expired (older than TTL).
     * 
     * @param ttlMillis Time to live in milliseconds
     * @return true if expired, false otherwise
     */
    public boolean isExpired(long ttlMillis) {
        return (System.currentTimeMillis() - timestamp) > ttlMillis;
    }
}
