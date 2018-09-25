package com.duobao.Cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LocalCacheUtils {
    LoadingCache<String, String> cache = CacheBuilder.newBuilder()
            .maximumSize(3).expireAfterWrite(20, TimeUnit.HOURS)
            .build(CacheLoaderCreatetor.createCacheLoader());

    public  void set(String key,String value) {
        try {
            cache.put(key,value);
        } catch (Exception e) {
            return;
        }
    }

    public  String get(String key) {
        try {
            String value = cache.getIfPresent(key);
            return value;
        } catch (Exception e) {
            return null;
        }
    }
}
