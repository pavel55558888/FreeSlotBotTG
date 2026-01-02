package example.ru.freeslotbottg.bot;

import example.ru.freeslotbottg.enums.MessageAndCallbackEnum;
import example.ru.freeslotbottg.util.BuilderMessage;
import example.ru.freeslotbottg.service.MessageHandlerAdminService;
import example.ru.freeslotbottg.service.MessageHandlerMasterService;
import example.ru.freeslotbottg.service.MessageHandlerUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageHandler {

    private final MessageHandlerUserService userService;
    private final MessageHandlerAdminService adminService;
    private final MessageHandlerMasterService masterService;
    private final BuilderMessage builderMessage;

    public List<BotApiMethod<?>> handle(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String firstName = update.getMessage().getFrom().getFirstName();
        String username = update.getMessage().getFrom().getUserName();

        log.info("Selected: {}", text);
        return switch (text) {
            case ("/start") -> userService.caseStart(chatId, firstName);
            case ("/my_slots") -> userService.caseMySlots(chatId, username);
            case ("/canceled_slot") -> userService.caseCanceledSlot(chatId, username);
            case String s when s.startsWith("/admin/new/master/") -> adminService.caseNewMaster(username, chatId, text);
            case String s when s.startsWith("/admin/delete/master/") -> adminService.caseDeleteMaster(chatId, username, text);
            case ("/admin") -> adminService.caseAdminInfo(chatId, username);
            case ("/slot/all") -> masterService.caseCheckAllSlots(chatId, username);
            case ("/slot/delete") -> masterService.caseDeleteSlot(chatId, username);
            case String s when s.startsWith("/slot") -> masterService.caseSetSlot(chatId, username, text);
            case ("/master") -> masterService.caseMasterInfo(chatId, username);
            default -> builderMessage.buildMessage(MessageAndCallbackEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
        };
    }
}