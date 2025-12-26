package example.ru.freeslotbottg.database.service.impl;

import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.repo.SlotRepo;
import example.ru.freeslotbottg.database.service.slots.UpdateSlot;
import example.ru.freeslotbottg.database.service.slots.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SlotByIdServiceImplClient implements SetSlot, GetAllSlotsByStaff, GetSlotById, UpdateSlot,
        GetSlotsByUsernameClient, GetAllSlots, DeleteSlotById {
    private SlotRepo slotRepo;

    @Override
    public List<SlotModel> getAllSlotsByStaff(StaffModel staff) {
        return slotRepo.getAllSlotsByStaff(staff);
    }

    @Override
    public void setSlots(SlotModel slots) {
        slotRepo.setSlots(slots);
    }

    @Override
    public SlotModel getSlotById(long id) {
        return slotRepo.getSlotById(id);
    }

    @Override
    public void updateSlot(SlotModel slot) {
        slotRepo.updateSlot(slot);
    }

    @Override
    public List<SlotModel> getSlotsByUsername(String username) {
        return slotRepo.getSlotsByUsername(username);
    }

    @Override
    public List<SlotModel> getSlots() {
        return slotRepo.getSlots();
    }

    @Override
    public void deleteSlot(long id) {
        slotRepo.deleteSlot(slotRepo.getSlotById(id));
    }
}
