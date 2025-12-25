package example.ru.freeslotbottg.database.repo;

import example.ru.freeslotbottg.database.model.StaffModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface StaffRepo {
    public List<StaffModel> getStaffByProfessionId(long id);
    public void setStaff(StaffModel staff);
    public StaffModel getStaffByFirstNameAndLastName(String firstNameAndLastName);
}
