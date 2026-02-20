package example.ru.freeslotbottg.database.service.client;

import example.ru.freeslotbottg.database.model.ClientModel;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface GetClientByUsername {
    public Optional<ClientModel> getClientByUsername(String username);
}
