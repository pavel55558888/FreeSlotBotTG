package example.ru.freeslotbottg.database.service.staff;

import example.ru.freeslotbottg.database.model.StaffModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface UpdateStaff {
    @Transactional
    public void updateStaff(StaffModel staff);
}
