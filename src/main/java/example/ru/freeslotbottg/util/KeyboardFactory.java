package example.ru.freeslotbottg.util;

import example.ru.freeslotbottg.database.model.SlotModel;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class KeyboardFactory {

    public InlineKeyboardMarkup createKeyboard(List<?> items, String prefix) {
        var rows = items.stream()
                .map(item -> {
                    String callbackData = prefix + ":" + item;
                    if (callbackData.getBytes().length > 60) {
                        callbackData = callbackData.substring(0, 50) + "...";
                    }
                    return InlineKeyboardButton.builder()
                            .text(String.valueOf(item))
                            .callbackData(callbackData)
                            .build();
                })
                .map(List::of)
                .toList();

        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup buildSlotKeyboard(List<SlotModel> slots, String prefix, boolean onlyDateAndTimeDisplay, boolean status) {
        var rows = slots.stream()
                .map(slot -> {
                    String display =
                            onlyDateAndTimeDisplay
                                    ? slot.getDate() + " " + slot.getTime() + (status ? (slot.isAvailable() ? " Свободно" : " Занято") : "")
                                    : slot.getStaffModel().getProfession().getProfession_type() + " "
                                    + slot.getDate() + " " + slot.getTime() + "\n";
                    String callbackData = prefix + ":" + slot.getId();

                    return InlineKeyboardButton.builder()
                            .text(display)
                            .callbackData(callbackData)
                            .build();
                })
                .map(List::of)
                .toList();

        return new InlineKeyboardMarkup(rows);
    }
}