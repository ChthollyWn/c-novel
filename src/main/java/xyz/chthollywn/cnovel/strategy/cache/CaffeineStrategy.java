package xyz.chthollywn.cnovel.strategy.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component("caffeine")
public class CaffeineStrategy implements CacheStrategy{
    private static final String FANQIE_SID_TT = "fanqie_sid_tt";

    private final Cache<String, Object> cache;

    public CaffeineStrategy() {
        this.cache = Caffeine.newBuilder()
                .maximumSize(1000)
                .build();
    }

    @Override
    public void setFanqieSid_tt(String sid_tt) {
        cache.put(FANQIE_SID_TT, sid_tt);
    }

    @Override
    public String getFanqieSid_tt() {
        return (String) cache.getIfPresent(FANQIE_SID_TT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void saveMessage(String userId, String message) {
        List<String> messages = (List<String>) cache.getIfPresent(userId);
        if (CollectionUtils.isEmpty(messages)) messages = Lists.newArrayList();
        messages.add(message);
        cache.put(userId, messages);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getMessages(String userId) {
        return (List<String>) cache.getIfPresent(userId);
    }
}
