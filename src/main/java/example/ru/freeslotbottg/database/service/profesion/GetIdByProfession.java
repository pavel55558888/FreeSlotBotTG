package example.ru.freeslotbottg.database.service.profesion;

import example.ru.freeslotbottg.database.model.ProfessionModel;
import org.springframework.stereotype.Service;

@Service
public interface GetIdByProfession {
    public ProfessionModel getIdByProfession(String profession);
}
