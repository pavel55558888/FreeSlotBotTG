package example.ru.freeslotbottg.database.repo;

import example.ru.freeslotbottg.database.model.StaffModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepo {
    public List<StaffModel> getStaffByProfessionId(long id, boolean pagination, int page, int size);
    public void setStaff(StaffModel staff);
    public StaffModel getStaffByFirstNameAndLastName(String firstNameAndLastName);
    public Optional<StaffModel> getStaffByUsername(String username);
    public void updateStaff(StaffModel staff);
    public void deleteStaff(long id);
}
