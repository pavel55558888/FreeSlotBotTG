package example.ru.freeslotbottg.database.service.staff;

import example.ru.freeslotbottg.database.model.StaffModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GetStaffByUsername {
    public Optional<StaffModel> getStaffByUsername(String username);
}
