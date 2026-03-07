package example.ru.freeslotbottg.database.repo;

import example.ru.freeslotbottg.database.model.ClientModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepo {
    public void setClient(ClientModel client);
    public Optional<ClientModel> getClientByUserId(long userId);
    public void updateClient(ClientModel client);
}
