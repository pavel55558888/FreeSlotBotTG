package example.ru.freeslotbottg.database.repo;

import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlotRepo {
    public List<SlotModel> getAllSlotsByStaff(StaffModel staff);
    public void setSlots(SlotModel slots);
    public SlotModel getSlotById(long id);
    public void updateSlot(SlotModel slot);
    public List<SlotModel> getSlotsByUsername(String username);
    public List<SlotModel> getSlots();
    public void deleteSlot(SlotModel slotModel);
}
