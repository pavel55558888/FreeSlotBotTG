package example.ru.freeslotbottg.database.repo.impl;

import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.repo.StaffRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class StaffRepoImpl implements StaffRepo {
    private final EntityManager entityManager;

    @Override
    public List<StaffModel> getStaffByProfessionId(long id) {
        String jpql = "SELECT s FROM StaffModel s WHERE s.profession.id = :professionId";
        TypedQuery<StaffModel> query = entityManager.createQuery(jpql, StaffModel.class);
        query.setParameter("professionId", id);
        return query.getResultList();
    }

    @Override
    public void setStaff(StaffModel staff) {
        entityManager.persist(staff);
    }

    @Override
    public StaffModel getStaffByFirstNameAndLastName(String firstNameAndLastName) {
        String[] parts = firstNameAndLastName.split(" ", 2);
        return entityManager.createQuery(
                        "SELECT s FROM StaffModel s WHERE s.firstName = :firstName AND s.lastName = :lastName",
                        StaffModel.class
                )
                .setParameter("firstName", parts[0])
                .setParameter("lastName", parts[1])
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public Optional<StaffModel> getStaffByUsername(String username) {
        return entityManager.createQuery("from StaffModel where username = :username", StaffModel.class)
                .setParameter("username", username)
                .getResultList()
                .stream()
                .findFirst();
    }
}
