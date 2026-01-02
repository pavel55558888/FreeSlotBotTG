package example.ru.freeslotbottg.database.service.slots;

import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GetAllSlotsByStaff {
    public List<SlotModel> getAllSlotsByStaff(StaffModel staff, Boolean isAvailable, boolean pagination, int page, int size);
}
