package example.ru.freeslotbottg.database.service.client;

import example.ru.freeslotbottg.database.model.ClientModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GetClients {
    public List<ClientModel> getClients(boolean isPagination, int page, int size);
}
