package example.ru.freeslotbottg.database.service.impl;

import example.ru.freeslotbottg.database.model.ProfessionModel;
import example.ru.freeslotbottg.database.repo.ProfessionRepo;
import example.ru.freeslotbottg.database.service.profesion.DeleteProfession;
import example.ru.freeslotbottg.database.service.profesion.GetAllProfession;
import example.ru.freeslotbottg.database.service.profesion.GetByProfession;
import example.ru.freeslotbottg.database.service.profesion.SetProfession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProfessionServiceImpl implements GetAllProfession, GetByProfession, SetProfession, DeleteProfession {
    private ProfessionRepo professionRepo;

    @Override
    public ProfessionModel getByProfession(String profession) {
        return professionRepo.getByProfession(profession);
    }

    @Override
    public void setProfession(ProfessionModel professions) {
        professionRepo.setProfession(professions);
    }

    @Override
    public void deleteProfession(long id) {
        professionRepo.deleteProfession(id);
    }

    @Override
    public List<ProfessionModel> getAllProfessions(boolean pagination, int page, int size) {

        return professionRepo.getAllProfessions(pagination, page, size);
    }
}
