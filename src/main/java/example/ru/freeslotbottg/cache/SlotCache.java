package example.ru.freeslotbottg.cache;

import example.ru.freeslotbottg.cache.model.SlotCacheModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
@Slf4j
public class SlotCache {
    private final Map<String, SlotCacheModel> slotCache = new ConcurrentHashMap<>();

    public void setCache(String username, SlotCacheModel slotCacheModel) {
        log.info("Setting cache username {} to {}", username, slotCacheModel);
        slotCache.put(username, slotCacheModel);
    }

    public Optional<SlotCacheModel> getCache(String username) {
        log.info("Getting cache username {}", username);
        return Optional.ofNullable(slotCache.get(username));
    }

    public void removeCache(String username) {
        log.info("Removing cache username {}", username);
        slotCache.remove(username);
    }
}
