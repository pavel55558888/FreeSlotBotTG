package example.ru.freeslotbottg.database.service.impl;

import example.ru.freeslotbottg.database.model.ClientModel;
import example.ru.freeslotbottg.database.repo.ClientRepo;
import example.ru.freeslotbottg.database.service.client.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ClientServiceImpl implements GetClients, GetClientByUsername, SetClient, UpdateClient, GetClientsCount {
    private final ClientRepo clientRepo;
    @Override
    public Optional<ClientModel> getClientByUsername(String username) {
        return clientRepo.getClientByUsername(username);
    }

    @Override
    public List<ClientModel> getClients(boolean isPagination, int page, int size) {
        return clientRepo.getClients(isPagination, page, size);
    }

    @Override
    public void setClient(ClientModel client) {
        clientRepo.setClient(client);
    }

    @Override
    public void updateClient(ClientModel client) {
        clientRepo.updateClient(client);
    }

    @Override
    public long getClientsCount() {
        return clientRepo.getClientsCount();
    }
}
