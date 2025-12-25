package example.ru.freeslotbottg.database.service.staff;

import example.ru.freeslotbottg.database.model.StaffModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GetStaffByProfessionId {
    public List<StaffModel> getStaffByProfessionId(long id);
}
