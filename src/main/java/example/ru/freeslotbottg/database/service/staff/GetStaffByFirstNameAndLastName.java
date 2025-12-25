package example.ru.freeslotbottg.database.service.staff;

import example.ru.freeslotbottg.database.model.StaffModel;
import org.springframework.stereotype.Service;

@Service
public interface GetStaffByFirstNameAndLastName {
    public StaffModel getStaffByFirstNameAndLastName(String firstNameAndLastName);
}
