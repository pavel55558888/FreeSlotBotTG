package example.ru.freeslotbottg.database.service.slots;

import example.ru.freeslotbottg.database.model.SlotModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UpdateSlot {
    @Transactional
    public void updateSlot(SlotModel slot);
}
