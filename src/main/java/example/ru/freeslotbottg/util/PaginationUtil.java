package example.ru.freeslotbottg.util;

import example.ru.freeslotbottg.database.model.ProfessionModel;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaginationUtil {
    private final KeyboardFactory keyboardFactory;

    public List<BotApiMethod<?>> paginationKeyboard(List<BotApiMethod<?>> actions, int page, long chatId, int messageId,
                                                    String prefix, List<?> objectForKeyboard, boolean isSlots,
                                                    boolean onlyDateAndTimeDisplay, boolean status, String text){

        if (objectForKeyboard.isEmpty() || !isValidObjectType(objectForKeyboard)){
            return actions;
        }

        actions.add(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(text)
                .replyMarkup(
                        isSlots ?
                                keyboardFactory.buildSlotKeyboard((List<SlotModel>) objectForKeyboard, prefix, page , onlyDateAndTimeDisplay, status)
                                : keyboardFactory.createKeyboard(objectForKeyboard, prefix, page))
                .build());

        return actions;
    }

    public int indexingPage(String value, int page){
        if (value.equals("next")) {
            page++;
        } else if (value.equals("back")) {
            page--;
        }
        return page;
    }

    private boolean isValidObjectType(List<?> list) {
        return list.stream().allMatch(item ->
                item instanceof ProfessionModel ||
                        item instanceof StaffModel ||
                        item instanceof SlotModel
        );
    }
}
