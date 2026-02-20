package example.ru.freeslotbottg.database.service.client;

import example.ru.freeslotbottg.database.model.ClientModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public interface UpdateClient {
    @Transactional
    public void updateClient(ClientModel client);
}
