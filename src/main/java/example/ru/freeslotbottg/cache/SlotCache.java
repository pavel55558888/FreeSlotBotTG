package example.ru.freeslotbottg.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import example.ru.freeslotbottg.cache.model.SlotCacheModel;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Getter
@Slf4j
public class SlotCache {

    @Value("${slot.cache.max.size}")
    private int maxSize;
    @Value("${slot.cache.max.time.life.hours}")
    private int maxTimeLife;

    private Cache<String, SlotCacheModel> slotCache;

    @PostConstruct
    private void init() {
        slotCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(maxTimeLife, TimeUnit.HOURS)
                .recordStats()
                .build();
    }

    public void setCache(String username, SlotCacheModel slotCacheModel) {
        log.info("Setting cache username {} to {}", username, slotCacheModel);
        slotCache.put(username, slotCacheModel);
    }

    public Optional<SlotCacheModel> getCache(String username) {
        log.info("Getting cache username {}", username);
        return Optional.ofNullable(slotCache.getIfPresent(username));
    }

    public void removeCache(String username) {
        log.info("Removing cache username {}", username);
        slotCache.invalidate(username);
    }
}
