package example.ru.freeslotbottg.scheduler;

import example.ru.freeslotbottg.cache.UserStateCache;
import example.ru.freeslotbottg.cache.model.UserStateCacheModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyRemoveCache {
    private final UserStateCache userStateCache;

    @Scheduled(cron = "${scheduler.remove.cache.user.state.crone}")
    private void cleanerCache(){
        log.info("Запуск ежедневной очистки кэша");
        long now = System.currentTimeMillis();
        long twelveHoursInMillis = TimeUnit.HOURS.toMillis(12);

        Map<Long, UserStateCacheModel> cache = userStateCache.getUserStateCache();
        Iterator<Map.Entry<Long, UserStateCacheModel>> iterator = cache.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Long, UserStateCacheModel> entry = iterator.next();
            UserStateCacheModel state = entry.getValue();

            if (now - state.getLastAction() > twelveHoursInMillis) {
                userStateCache.removeCache(entry.getKey());
            }
        }
    }
}
