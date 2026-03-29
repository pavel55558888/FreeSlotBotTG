package example.ru.freeslotbottg.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import example.ru.freeslotbottg.cache.model.JobEvaluationCacheModel;
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
public class JobEvaluationCache {
    @Value("${job.evaluation.cache.max.size}")
    private int maxSize;
    @Value("${job.evaluation.cache.max.time.life.hours}")
    private int maxTimeLife;

    private Cache<String, JobEvaluationCacheModel> jobEvaluationCache;

    @PostConstruct
    private void init() {
        jobEvaluationCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(maxTimeLife, TimeUnit.HOURS)
                .recordStats()
                .build();
    }

    public void setCache(String username, JobEvaluationCacheModel jobEvaluationCacheModel) {
        log.info("Setting cache username {} to {}", username, jobEvaluationCacheModel);
        jobEvaluationCache.put(username, jobEvaluationCacheModel);
    }

    public Optional<JobEvaluationCacheModel> getCache(String username) {
        log.info("Getting cache username {}", username);
        return Optional.ofNullable(jobEvaluationCache.getIfPresent(username));
    }

    public void removeCache(String username) {
        log.info("Removing cache username {}", username);
        jobEvaluationCache.invalidate(username);
    }
}
