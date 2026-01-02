package example.ru.freeslotbottg.util;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
public class BuilderMessage {

    public List<BotApiMethod<?>> buildMessage(String message, long chatId){
        return List.of(SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build());
    }
}
