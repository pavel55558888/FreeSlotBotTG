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
    public List<ProfessionModel> getAllProfessions(boolean pagination, int page, int size) {
        if (pagination) {
            return entityManager
                    .createQuery("from ProfessionModel", ProfessionModel.class)
                    .setFirstResult(page*size)
                    .setMaxResults(size)
                    .getResultList();
        }
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

    @Override
    public void deleteProfession(long id) {
        entityManager.remove(entityManager.find(ProfessionModel.class, id));
    }
}
