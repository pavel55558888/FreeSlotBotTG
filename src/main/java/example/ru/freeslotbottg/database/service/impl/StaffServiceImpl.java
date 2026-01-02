package example.ru.freeslotbottg.database.service.impl;

import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.repo.StaffRepo;
import example.ru.freeslotbottg.database.service.staff.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StaffServiceImpl implements SetStaff, GetStaffByProfessionId, GetStaffByFirstNameAndLastName,
        GetStaffByUsername, UpdateStaff, DeleteStaff {
    private StaffRepo staffRepo;

    @Override
    public List<StaffModel> getStaffByProfessionId(long id, boolean pagination, int page, int size) {
        return staffRepo.getStaffByProfessionId(id, pagination, page, size);
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

    @Override
    public void updateStaff(StaffModel staff) {
        staffRepo.updateStaff(staff);
    }

    @Override
    public void deleteStaff(long id) {
        staffRepo.deleteStaff(id);
    }
}
