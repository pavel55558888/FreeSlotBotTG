package example.ru.freeslotbottg.database.repo;

import example.ru.freeslotbottg.database.model.ProfessionModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfessionRepo {
    public List<ProfessionModel> getAllProfessions(boolean pagination, int page, int size);
    public ProfessionModel getByProfession(String profession);
    public void setProfession(ProfessionModel professions);
    public void deleteProfession(long id);
}
