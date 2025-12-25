package example.ru.freeslotbottg.database.service.impl;

import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.repo.StaffRepo;
import example.ru.freeslotbottg.database.service.staff.GetStaffByFirstNameAndLastName;
import example.ru.freeslotbottg.database.service.staff.GetStaffByProfessionId;
import example.ru.freeslotbottg.database.service.staff.SetStaff;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StaffServiceImpl implements SetStaff, GetStaffByProfessionId, GetStaffByFirstNameAndLastName {
    private StaffRepo staffRepo;

    @Override
    public List<StaffModel> getStaffByProfessionId(long id) {
        return staffRepo.getStaffByProfessionId(id);
    }

    @Override
    public void setStaff(StaffModel staff) {
        staffRepo.setStaff(staff);
    }

    @Override
    public StaffModel getStaffByFirstNameAndLastName(String firstNameAndLastName) {
        return staffRepo.getStaffByFirstNameAndLastName(firstNameAndLastName);
    }
}
