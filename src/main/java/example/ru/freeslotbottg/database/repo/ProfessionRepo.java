package example.ru.freeslotbottg.database.repo;

import example.ru.freeslotbottg.database.model.ProfessionModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ProfessionRepo {
    public List<ProfessionModel> getAllProfessions();
    public ProfessionModel getIdByProfession(String profession);
    public void setProfession(ProfessionModel professions);
}
