package example.ru.freeslotbottg.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Service
public class BuilderMessageService {

    public List<BotApiMethod<?>> buildMessage(String message, long chatId){
        return List.of(SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build());
    }
}
