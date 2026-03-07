package example.ru.freeslotbottg.database.repo.impl;

import example.ru.freeslotbottg.database.model.ClientModel;
import example.ru.freeslotbottg.database.repo.ClientRepo;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class ClientRepoImpl implements ClientRepo {
    private final EntityManager entityManager;

    public void setClient(ClientModel client) {
        entityManager.persist(client);
    }

    @Override
    public Optional<ClientModel> getClientByUserId(long userId) {
        return Optional.ofNullable(entityManager
                .createQuery("from ClientModel where chatId = :userId", ClientModel.class)
                .setParameter("userId", userId)
                .getSingleResultOrNull());
    }

    @Override
    public void updateClient(ClientModel client) {
        entityManager.merge(client);
    }
}
