package example.ru.freeslotbottg.cache;

import example.ru.freeslotbottg.cache.model.JobEvaluationCacheModel;
import example.ru.freeslotbottg.cache.model.SlotCacheModel;
import example.ru.freeslotbottg.database.model.SlotModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter
@Slf4j
public class JobEvaluationCache {
    private final Map<String, JobEvaluationCacheModel> jobEvaluationCache = new ConcurrentHashMap<>();

    public void setCache(String username, JobEvaluationCacheModel jobEvaluationCacheModel) {
        log.info("Setting cache username {} to {}", username, jobEvaluationCacheModel);
        jobEvaluationCache.put(username, jobEvaluationCacheModel);
    }

    public Optional<JobEvaluationCacheModel> getCache(String username) {
        log.info("Getting cache username {}", username);
        return Optional.ofNullable(jobEvaluationCache.get(username));
    }

    public void removeCache(String username) {
        log.info("Removing cache username {}", username);
        jobEvaluationCache.remove(username);
    }
}
