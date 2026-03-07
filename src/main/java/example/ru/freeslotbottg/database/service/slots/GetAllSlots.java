package example.ru.freeslotbottg.database.service.slots;

import example.ru.freeslotbottg.database.model.SlotModel;

import java.util.List;

public interface GetAllSlots {
    public List<SlotModel> getSlots(boolean isPagination, int page, int size);
}
