package example.ru.freeslotbottg.util;

import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.enums.MonthEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class KeyboardFactory {

    public InlineKeyboardMarkup createKeyboard(List<?> items, String prefix, int page) {
        boolean pagination = !(items.size() < 5 && page == 0);

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
                .collect(Collectors.toList());

        if (pagination) {
            rows.add(generateButtonPagination(prefix, page));
        }

        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup buildSlotKeyboard(
            List<SlotModel> slots,
            String prefix,
            int page,
            boolean onlyDateAndTimeDisplay,
            boolean status) {
        boolean pagination = !(slots.size() < 5 && page == 0);

        var rows = slots.stream()
                .map(slot -> {
                    String display =
                            onlyDateAndTimeDisplay
                                    ? slot.getDate().getDayOfMonth() + " "
                                    + MonthEnum.getByNumber(slot.getDate().getMonthValue()).getMonth() + " " + slot.getTime()
                                    + " " + (status ? (slot.isAvailable() ? " -> Свободно" : " -> Занято") : "")
                                    : slot.getStaffModel().getProfession().getProfession_type() + " -> "
                                    + slot.getDate().getDayOfMonth() + " "
                                    + MonthEnum.getByNumber(slot.getDate().getMonthValue()).getMonth() + " " + slot.getTime() + "\n";
                    String callbackData = prefix + ":" + slot.getId();

                    return InlineKeyboardButton.builder()
                            .text(display)
                            .callbackData(callbackData)
                            .build();
                })
                .map(List::of)
                .collect(Collectors.toList());

        if (pagination) {
            rows.add(generateButtonPagination(prefix, page));
        }

        return new InlineKeyboardMarkup(rows);
    }

    private List<InlineKeyboardButton> generateButtonPagination(String prefix, int page) {
        List<InlineKeyboardButton> paginationRow = new ArrayList<>();
        paginationRow.add(InlineKeyboardButton.builder()
                .text("Назад")
                .callbackData(prefix + ":back:" + page)

                .build());
        paginationRow.add(InlineKeyboardButton.builder()
                .text("Вперёд")
                .callbackData(prefix + ":next:" + page)
                .build());
        return paginationRow;
    }


}