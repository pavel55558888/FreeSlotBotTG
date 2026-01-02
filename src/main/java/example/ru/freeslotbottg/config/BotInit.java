package example.ru.freeslotbottg.config;

import example.ru.freeslotbottg.bot.TelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@AllArgsConstructor
public class BotInit {
    private TelegramBot telegramBot;

    @PostConstruct
    @SneakyThrows
    public void botInit() {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(telegramBot);
        System.out.println("<> Telegram bot started");
    }
}
