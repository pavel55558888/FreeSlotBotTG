package example.ru.freeslotbottg.cache;

import example.ru.freeslotbottg.cache.model.UserStateModel;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
public class UserStateCache {
    private final Map<Long, UserStateModel> userStateCache = new ConcurrentHashMap<>();

    public void setCache(Long chatId, UserStateModel userState) {
        userStateCache.put(chatId, userState);
    }

    public Optional<UserStateModel> getCache(Long chatId) {
        return Optional.ofNullable(userStateCache.get(chatId));
    }
}
