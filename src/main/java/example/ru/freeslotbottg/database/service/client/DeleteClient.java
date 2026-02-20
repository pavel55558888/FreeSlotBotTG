package example.ru.freeslotbottg.database.service.client;

import example.ru.freeslotbottg.database.model.ClientModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface DeleteClient {
    @Transactional
    public void deleteClient(ClientModel client);
}
