package example.ru.freeslotbottg.bot;

import example.ru.freeslotbottg.config.BotConfig;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final MessageHandler messageHandler;
    private final CallbackHandler callbackHandler;

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            List<BotApiMethod<?>> actions = messageHandler.handle(update);
            for (BotApiMethod<?> action : actions) {
                execute(action);
            }
        } else if (update.hasCallbackQuery()) {
            List<BotApiMethod<?>> actions = callbackHandler.handle(update.getCallbackQuery());
            for (BotApiMethod<?> action : actions) {
                execute(action);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onRegister() {
        System.out.println("<> TelegramBot onRegister");
    }
}