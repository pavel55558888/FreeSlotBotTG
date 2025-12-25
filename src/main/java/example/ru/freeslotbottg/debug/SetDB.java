package example.ru.freeslotbottg.debug;

import example.ru.freeslotbottg.database.model.ProfessionModel;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.service.slots.GetAllSlotsByStaff;
import example.ru.freeslotbottg.database.service.profesion.SetProfession;
import example.ru.freeslotbottg.database.service.slots.SetSlot;
import example.ru.freeslotbottg.database.service.staff.SetStaff;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
@AllArgsConstructor
public class SetDB {
    private SetProfession setProfession;
    private SetStaff setStaff;
    private SetSlot setSlot;
    private GetAllSlotsByStaff getAllSlotsByStaff;
    @PostConstruct
    public void init() {
//        ProfessionModel profession = new ProfessionModel("Маникюр");
//        setProfession.setProfession(profession);
//        StaffModel staffModel = new StaffModel(profession, "Сахарова", "Олеся");
//        setStaff.setStaff(staffModel);
//        for (int i = 0; i <= 20; i++)
//            setSlot.setSlots(new SlotModel(staffModel, LocalDate.of(2025, 12, 25), LocalTime.of(14, 30)));
//
//        System.out.println(getAllSlotsByStaff.getAllSlotsByStaff(staffModel));
    }
}
