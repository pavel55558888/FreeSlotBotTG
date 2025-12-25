package example.ru.freeslotbottg.database.service.impl;

import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.repo.StaffRepo;
import example.ru.freeslotbottg.database.service.staff.GetStaffByFirstNameAndLastName;
import example.ru.freeslotbottg.database.service.staff.GetStaffByProfessionId;
import example.ru.freeslotbottg.database.service.staff.GetStaffByUsername;
import example.ru.freeslotbottg.database.service.staff.SetStaff;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StaffServiceImpl implements SetStaff, GetStaffByProfessionId, GetStaffByFirstNameAndLastName, GetStaffByUsername {
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

    @Override
    public Optional<StaffModel> getStaffByUsername(String username) {
        return staffRepo.getStaffByUsername(username);
    }
}
