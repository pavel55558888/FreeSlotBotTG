package example.ru.freeslotbottg.database.repo.impl;

import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.repo.SlotRepo;
import jakarta.persistence.EntityManager;
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
    public List<SlotModel> getAllSlotsByStaff(StaffModel staff) {
        String jpql = """
        SELECT s FROM SlotModel s
        WHERE s.staffModel = :staff
        ORDER BY s.date, s.time
        """;

        return entityManager.createQuery(jpql, SlotModel.class)
                .setParameter("staff", staff)
                .getResultList();
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
    public List<SlotModel> getSlotsByUsername(String username) {
        return entityManager.createQuery("from SlotModel where usernameClient = :username", SlotModel.class)
                .setParameter("username", username)
                .getResultList();
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
