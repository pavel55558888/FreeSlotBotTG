package example.ru.freeslotbottg.database.service.profesion;

import example.ru.freeslotbottg.database.model.ProfessionModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GetAllProfession {
    public List<ProfessionModel> getAllProfessions(boolean pagination, int page, int size);
}
