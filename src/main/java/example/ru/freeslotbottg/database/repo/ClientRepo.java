package example.ru.freeslotbottg.database.repo;

import example.ru.freeslotbottg.database.model.ClientModel;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepo {
    public void setClient(ClientModel client);
    public List<ClientModel> getClients(boolean isPagination, int page, int size);
    public Optional<ClientModel> getClientByUsername(String username);
    public void updateClient(ClientModel client);
    public long getClientsCount();
}
