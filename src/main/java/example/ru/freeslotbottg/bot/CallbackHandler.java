package example.ru.freeslotbottg.bot;

import example.ru.freeslotbottg.service.CallbackHandlerMasterService;
import example.ru.freeslotbottg.service.CallbackHandlerUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CallbackHandler {
    private final CallbackHandlerMasterService callbackHandlerMasterService;
    private final CallbackHandlerUserService callbackHandlerUserService;

    public List<BotApiMethod<?>> handle(CallbackQuery query) {
        String data = query.getData();
        long chatId = query.getMessage().getChatId();
        int messageId = query.getMessage().getMessageId();
        String username = query.getFrom().getUserName();
        String firstName = query.getFrom().getFirstName().isEmpty() ? "" : query.getFrom().getFirstName();
        String lastName = query.getFrom().getLastName();
        log.info("Data: {},chatId: {}, messageId: {}, username: {}, firstName: {}, lastName: {}",
                data, chatId, messageId, username, firstName, lastName);

        List<BotApiMethod<?>> actions = new ArrayList<>();
        actions.add(AnswerCallbackQuery.builder()
                .callbackQueryId(query.getId())
                .build());

        if (data == null) {
            return actions;
        }

        String[] parts = data.split(":");
        if (parts.length != 2 && parts.length != 3) {
            return actions;
        }

        String action = parts[0];
        String value = parts[1];
        String valueTime = parts.length > 2 ? parts[2] : null;

        int page = parts.length > 2 ? Integer.parseInt(parts[2]) : -1;
        log.info("Selected -> action: {}, value: {}, page: {}", action, value, page);

        return switch (action){
            case ("activity") -> callbackHandlerUserService.caseActivity(actions, chatId, messageId, value, page, action);
            case ("master") -> callbackHandlerUserService.caseMaster(actions, chatId, messageId, value, page, action, username);
            case ("slot") -> callbackHandlerUserService.caseSlot(actions, chatId, messageId, value, username, firstName, lastName, page, action);
            case ("canceled") -> callbackHandlerUserService.caseCanceled(actions, chatId, messageId, value, firstName, lastName, page, action, username);
            case ("delete") -> callbackHandlerMasterService.caseDelete(actions, chatId, messageId, value, page, action);
            case ("check_slot") -> callbackHandlerMasterService.caseCheckSlot(actions, chatId, messageId, value, page, action);
            case ("setSlotMonth") -> callbackHandlerMasterService.caseSetSlotGetDays(actions, chatId, messageId, value, username);
            case ("setSlotDay") -> callbackHandlerMasterService.caseSetSlotGetHours(actions, chatId, messageId, value, username);
            case ("setSlotHours") -> callbackHandlerMasterService.caseSetSlotGetHoursAndTimes(actions, chatId, messageId, value, username);
            case ("setSlotMinute") -> callbackHandlerMasterService.caseSetSlotFinale(actions, chatId, messageId, value, valueTime, username);
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