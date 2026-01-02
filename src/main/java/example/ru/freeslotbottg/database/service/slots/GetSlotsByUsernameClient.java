package example.ru.freeslotbottg.database.service.slots;

import example.ru.freeslotbottg.database.model.SlotModel;

import java.util.List;

public interface GetSlotsByUsernameClient {
    public List<SlotModel> getSlotsByUsername(String username, boolean pagination, int page, int size);
}
