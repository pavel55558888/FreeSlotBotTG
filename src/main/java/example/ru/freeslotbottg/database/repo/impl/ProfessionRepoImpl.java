package example.ru.freeslotbottg.database.repo.impl;

import example.ru.freeslotbottg.database.model.ProfessionModel;
import example.ru.freeslotbottg.database.repo.ProfessionRepo;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@AllArgsConstructor
public class ProfessionRepoImpl implements ProfessionRepo {
    private final EntityManager entityManager;

    @Override
    public List<ProfessionModel> getAllProfessions() {
        return entityManager.createQuery("from ProfessionModel", ProfessionModel.class).getResultList();
    }

    @Override
    public ProfessionModel getByProfession(String profession) {
        return entityManager.createQuery(
                        "SELECT p FROM ProfessionModel p WHERE p.profession_type = :professionType",
                        ProfessionModel.class
                )
                .setParameter("professionType", profession)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public void setProfession(ProfessionModel professions) {
        entityManager.persist(professions);
    }
}
