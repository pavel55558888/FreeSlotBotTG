package example.ru.freeslotbottg.util;

import example.ru.freeslotbottg.database.service.profesion.GetAllProfession;
import example.ru.freeslotbottg.enums.MessageAndCallbackEnum;
import example.ru.freeslotbottg.enums.Pagination;
import example.ru.freeslotbottg.enums.SessionTimeHasExpiredEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SessionTimeHasExpired {
    private final KeyboardFactory keyboardFactory;
    private final GetAllProfession getAllProfession;

    public List<BotApiMethod<?>> sessionExpired(long chatId){
        SendMessage sessionExpired = SendMessage.builder()
                .chatId(chatId)
                .text(SessionTimeHasExpiredEnum.SESSION_TIME_HAS_EXPIRED.getTemplate())
                .build();

        SendMessage choice = SendMessage.builder()
                .chatId(chatId)
                .text(MessageAndCallbackEnum.CHOICE_PROFESSION.getTemplate())
                .replyMarkup(keyboardFactory.createKeyboard(
                        getAllProfession.getAllProfessions(
                                true,
                                Pagination.START_INDEX_PAGE.getTemplate(),
                                Pagination.PAGE_SIZE.getTemplate()
                        ), "activity", Pagination.START_INDEX_PAGE.getTemplate()))
                .build();

        return Arrays.asList(sessionExpired, choice);
    }
}
