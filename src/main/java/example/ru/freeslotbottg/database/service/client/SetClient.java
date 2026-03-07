package example.ru.freeslotbottg.database.service.client;


import example.ru.freeslotbottg.database.model.ClientModel;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SetClient {
    @Transactional
    public void setClient(ClientModel client);
}
