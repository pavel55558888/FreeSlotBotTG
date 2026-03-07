package example.ru.freeslotbottg.database.service.client;

import example.ru.freeslotbottg.database.model.ClientModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GetClientByUserId {
    public Optional<ClientModel> getClientByUserId(long userId);
}
