package com.duobao.Cache;

public class CacheLoaderCreatetor {

    public static com.google.common.cache.CacheLoader<String, String> createCacheLoader() {
        return new com.google.common.cache.CacheLoader<String, String>() {
            @Override
            public String load(String key) throws Exception {
                return null;
            }
        };
    }
}
