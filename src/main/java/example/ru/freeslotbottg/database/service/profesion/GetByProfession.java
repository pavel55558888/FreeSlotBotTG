package example.ru.freeslotbottg.database.service.profesion;

import example.ru.freeslotbottg.database.model.ProfessionModel;
import org.springframework.stereotype.Service;

@Service
public interface GetByProfession {
    public ProfessionModel getByProfession(String profession);
}
