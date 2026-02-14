package example.ru.freeslotbottg.scheduler;

import example.ru.freeslotbottg.cache.UserStateCache;
import example.ru.freeslotbottg.cache.model.UserStateModel;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DailyRemoveCache {
    private final UserStateCache userStateCache;

    @Scheduled(cron = "${scheduler.remove.cache.crone}")
    private void cleanerCache(){
        long now = System.currentTimeMillis();
        long twelveHoursInMillis = TimeUnit.HOURS.toMillis(12);

        Map<Long, UserStateModel> cache = userStateCache.getUserStateCache();
        Iterator<Map.Entry<Long, UserStateModel>> iterator = cache.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Long, UserStateModel> entry = iterator.next();
            UserStateModel state = entry.getValue();

            if (now - state.getLastAction() > twelveHoursInMillis) {
                iterator.remove();
            }
        }
    }
}
