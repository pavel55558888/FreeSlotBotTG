package example.ru.freeslotbottg.database.service.slots;

import example.ru.freeslotbottg.database.model.SlotModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface SetSlot {
    @Transactional
    public void setSlots(SlotModel slots);
}
