package example.ru.freeslotbottg.database.repo.impl;

import example.ru.freeslotbottg.database.model.ClientModel;
import example.ru.freeslotbottg.database.repo.ClientRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class ClientRepoImpl implements ClientRepo {
    private final EntityManager entityManager;

    public void setClient(ClientModel client) {
        entityManager.persist(client);
    }

    public List<ClientModel> getClients(
            boolean isPagination,
            int page,
            int size
    ) {
        List<Long> ids = entityManager.createQuery(
                        "select c.id from ClientModel c order by c.id",
                        Long.class
                )
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        return entityManager.createQuery(
                        "select distinct c from ClientModel c join fetch c.staffModelList where c.id in :ids",
                        ClientModel.class
                )
                .setParameter("ids", ids)
                .getResultList();
    }

    public Optional<ClientModel> getClientByUsername(String username) {
        List<ClientModel> list = entityManager.createQuery(
                "select distinct c from ClientModel c join fetch c.staffModelList where c.username = :username",
                ClientModel.class
        )
                .setParameter("username", username)
                .getResultList();

        return list.isEmpty() ? Optional.empty() : Optional.of(list.getFirst());
    }

    public void updateClient(ClientModel client) {
        entityManager.merge(client);
    }

    public long getClientsCount() {
        return entityManager.createQuery(
                "select count(c) from ClientModel c",
                Long.class
        ).getSingleResult();
    }

    @Override
    public void deleteClient(ClientModel client) {
        entityManager.remove(client);
    }
}
