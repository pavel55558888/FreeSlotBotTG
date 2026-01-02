package example.ru.freeslotbottg.database.repo.impl;

import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.repo.SlotRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
@AllArgsConstructor
public class SlotRepoImpl implements SlotRepo {
    private final EntityManager entityManager;

    @Override
    public List<SlotModel> getAllSlotsByStaff(StaffModel staff, Boolean isAvailable, boolean pagination, int page, int size) {

        StringBuilder jpql = new StringBuilder("""
        SELECT s FROM SlotModel s
        WHERE s.staffModel = :staff
        """);

        if (isAvailable != null) {
            jpql.append(" AND s.isAvailable = :isAvailable");
        }

        jpql.append(" ORDER BY s.date, s.time");

        TypedQuery<SlotModel> query = entityManager.createQuery(jpql.toString(), SlotModel.class)
                .setParameter("staff", staff);

        if (isAvailable != null) {
            query.setParameter("isAvailable", isAvailable);
        }

        if (pagination) {
            int offset = page * size;
            query.setFirstResult(offset);
            query.setMaxResults(size);
        }

        return query.getResultList();
    }

    @Override
    public void setSlots(SlotModel slots) {
        entityManager.persist(slots);
    }

    @Override
    public SlotModel getSlotById(long id) {
        return entityManager.find(SlotModel.class, id);
    }

    @Override
    public void updateSlot(SlotModel slot) {
        entityManager.merge(slot);
    }

    @Override
    public List<SlotModel> getSlotsByUsername(String username, boolean pagination, int page, int size) {
        TypedQuery<SlotModel> query = entityManager.createQuery(
                "SELECT s FROM SlotModel s WHERE s.usernameClient = :username",
                SlotModel.class
        ).setParameter("username", username);

        if (pagination) {
            int offset = page * size;
            query.setFirstResult(offset);
            query.setMaxResults(size);
        }

        return query.getResultList();
    }

    @Override
    public List<SlotModel> getSlots() {
        return entityManager.createQuery("from SlotModel", SlotModel.class).getResultList();
    }

    @Override
    public void deleteSlot(SlotModel slotModel) {
        entityManager.remove(slotModel);
    }
}
