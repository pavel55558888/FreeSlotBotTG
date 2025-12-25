package example.ru.freeslotbottg.database.service.profesion;

import example.ru.freeslotbottg.database.model.ProfessionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface SetProfession {
    @Transactional
    public void setProfession(ProfessionModel professions);
}
