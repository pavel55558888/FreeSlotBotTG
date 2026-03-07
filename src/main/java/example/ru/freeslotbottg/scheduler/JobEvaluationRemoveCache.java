package example.ru.freeslotbottg.scheduler;

import example.ru.freeslotbottg.cache.JobEvaluationCache;
import example.ru.freeslotbottg.cache.model.JobEvaluationCacheModel;
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
public class JobEvaluationRemoveCache {
    private final JobEvaluationCache jobEvaluationCache;

    @Scheduled(cron = "${scheduler.remove.cache.jov.evaluation.crone}")
    private void cleanerCache(){
        log.info("Запуск часовой очистки кэша оценки работы");
        long now = System.currentTimeMillis();
        long oneHour = TimeUnit.HOURS.toMillis(1);

        Map<String, JobEvaluationCacheModel> cache = jobEvaluationCache.getJobEvaluationCache();
        Iterator<Map.Entry<String, JobEvaluationCacheModel>> iterator = cache.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, JobEvaluationCacheModel> entry = iterator.next();
            JobEvaluationCacheModel jobEvaluationCacheModel = entry.getValue();

            if (now - jobEvaluationCacheModel.getStartAt() > oneHour) {
                jobEvaluationCache.removeCache(entry.getKey());
            }
        }
    }
}
