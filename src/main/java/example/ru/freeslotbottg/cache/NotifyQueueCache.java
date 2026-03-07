package example.ru.freeslotbottg.cache;

import example.ru.freeslotbottg.database.model.StaffModel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@Getter
@Slf4j
public class NotifyQueueCache {
    private final Deque<StaffModel> slotQueue = new ConcurrentLinkedDeque<>();

    public void setCache(StaffModel staff) {
        log.info("Setting queue: {}", staff);
        slotQueue.addLast(staff);
    }

    public Deque<StaffModel> getAllQueue() {
        return new ConcurrentLinkedDeque<>(slotQueue);
    }

    public void clearCache() {
        log.info("Clearing queue");
        slotQueue.clear();
    }
}
