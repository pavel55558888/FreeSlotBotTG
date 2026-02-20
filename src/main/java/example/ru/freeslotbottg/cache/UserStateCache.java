package example.ru.freeslotbottg.cache;

import example.ru.freeslotbottg.cache.model.UserStateCacheModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
@Slf4j
public class UserStateCache {
    private final Map<Long, UserStateCacheModel> userStateCache = new ConcurrentHashMap<>();

    public void setCache(Long chatId, UserStateCacheModel userState) {
        log.info("Setting cache for chat id {}", chatId);
        userStateCache.put(chatId, userState);
    }

    public Optional<UserStateCacheModel> getCache(Long chatId) {
        log.info("Get cache for chat id {}", chatId);
        return Optional.ofNullable(userStateCache.get(chatId));
    }

    public void removeCache(Long chatId) {
        log.info("Remove cache for chat id {}", chatId);
        userStateCache.remove(chatId);
    }
}
