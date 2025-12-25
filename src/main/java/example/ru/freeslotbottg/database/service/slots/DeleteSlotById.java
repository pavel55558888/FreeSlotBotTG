package example.ru.freeslotbottg.database.service.slots;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface DeleteSlotById {
    @Transactional
    public void deleteSlot(long id);
}
