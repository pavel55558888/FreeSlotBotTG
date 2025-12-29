package example.ru.freeslotbottg.bot;

import example.ru.freeslotbottg.service.CallbackHandlerMasterService;
import example.ru.freeslotbottg.service.CallbackHandlerUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class CallbackHandler {
    private final CallbackHandlerMasterService callbackHandlerMasterService;
    private final CallbackHandlerUserService callbackHandlerUserService;

    public List<BotApiMethod<?>> handle(CallbackQuery query) {
        String data = query.getData();
        long chatId = query.getMessage().getChatId();
        int messageId = query.getMessage().getMessageId();
        String username = query.getFrom().getUserName();
        String firstName = query.getFrom().getFirstName();
        String lastName = query.getFrom().getLastName();

        List<BotApiMethod<?>> actions = new ArrayList<>();
        actions.add(AnswerCallbackQuery.builder()
                .callbackQueryId(query.getId())
                .build());

        if (data == null) {
            return actions;
        }

        String[] parts = data.split(":", 2);
        if (parts.length != 2) {
            return actions;
        }

        String action = parts[0];
        String value = parts[1];

        return switch (action){
            case ("activity") -> callbackHandlerUserService.caseActivity(actions, chatId, messageId, value);
            case ("master") -> callbackHandlerUserService.caseMaster(actions, chatId, messageId, value);
            case ("slot") -> callbackHandlerUserService.caseSlot(actions, chatId, messageId, value, username, firstName, lastName);
            case ("canceled") -> callbackHandlerUserService.caseCanceled(actions, chatId, messageId, value, firstName, lastName);
            case ("delete") -> callbackHandlerMasterService.caseDelete(actions, chatId, messageId, value);
            case ("check_slot") -> callbackHandlerMasterService.caseCheckSlot(actions, chatId, value, firstName, lastName);
            default -> {
                actions.add(SendMessage.builder()
                        .chatId(chatId)
                        .text("Неизвестная ошибка: такой функционал не найден.")
                        .build());
                yield actions;
            }
        };
    }
}