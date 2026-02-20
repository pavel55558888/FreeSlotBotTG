package example.ru.freeslotbottg.scheduler;

import example.ru.freeslotbottg.cache.SlotCache;
import example.ru.freeslotbottg.cache.model.SlotCacheModel;
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
public class SlotRemoveCache {
    private final SlotCache slotCache;

    @Scheduled(cron = "${scheduler.remove.cache.slot.crone}")
    private void cleanerCache(){
        log.info("Запуск часовой очистки кэша");
        long now = System.currentTimeMillis();
        long oneHour = TimeUnit.HOURS.toMillis(1);

        Map<String, SlotCacheModel> cache = slotCache.getSlotCache();
        Iterator<Map.Entry<String, SlotCacheModel>> iterator = cache.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, SlotCacheModel> entry = iterator.next();
            SlotCacheModel slotCacheModel = entry.getValue();

            if (now - slotCacheModel.getStartAt() > oneHour) {
                slotCache.removeCache(entry.getKey());
            }
        }
    }
}
