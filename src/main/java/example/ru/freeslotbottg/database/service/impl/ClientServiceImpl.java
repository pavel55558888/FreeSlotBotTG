package example.ru.freeslotbottg.database.service.impl;

import example.ru.freeslotbottg.database.model.ClientModel;
import example.ru.freeslotbottg.database.repo.ClientRepo;
import example.ru.freeslotbottg.database.service.client.GetClientByUserId;
import example.ru.freeslotbottg.database.service.client.SetClient;
import example.ru.freeslotbottg.database.service.client.UpdateClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements SetClient, GetClientByUserId, UpdateClient {
    private ClientRepo clientRepo;

    @Override
    public void updateClient(ClientModel client) {
        clientRepo.updateClient(client);
    }

    @Override
    public void setClient(ClientModel client) {
        clientRepo.setClient(client);
    }

    @Override
    public Optional<ClientModel> getClientByUserId(long userId) {
        return clientRepo.getClientByUserId(userId);
    }
}
