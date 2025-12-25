package example.ru.freeslotbottg.database.service.slots;

import example.ru.freeslotbottg.database.model.SlotModel;

import java.util.List;

public interface GetSlotsByUsername {
    public List<SlotModel> getSlotsByUsername(String username);
}
