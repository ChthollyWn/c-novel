package xyz.chthollywn.cnovel.strategy.cache;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CacheService {
    @Resource
    private Map<String, CacheStrategy> cacheStrategyMap;

    public CacheStrategy getCacheStrategy() {
        return cacheStrategyMap.get("caffeine");
    }
}
