package example.ru.freeslotbottg.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import example.ru.freeslotbottg.cache.model.UserStateCacheModel;
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
public class UserStateCache {
    @Value("${user.state.cache.max.size}")
    private int maxSize;
    @Value("${user.state.cache.max.time.life.hours}")
    private int maxTimeLife;

    private Cache<Long, UserStateCacheModel> userStateCache;

    @PostConstruct
    private void init() {
        userStateCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(maxTimeLife, TimeUnit.HOURS)
                .recordStats()
                .build();
    }

    public void setCache(Long chatId, UserStateCacheModel userState) {
        log.info("Setting cache for chat id {}", chatId);
        userStateCache.put(chatId, userState);
    }

    public Optional<UserStateCacheModel> getCache(Long chatId) {
        log.info("Get cache for chat id {}", chatId);
        return Optional.ofNullable(userStateCache.getIfPresent(chatId));
    }

    public void removeCache(Long chatId) {
        log.info("Remove cache for chat id {}", chatId);
        userStateCache.invalidate(chatId);
    }
}
