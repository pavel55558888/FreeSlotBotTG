package example.ru.freeslotbottg.util;

import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.enums.MonthEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.YearMonth;
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
                                    + MonthEnum.getByNumber(slot.getDate().getMonthValue()).getMonthGenitive() + " " + slot.getTime()
                                    + " " + (status ? (slot.isAvailable() ? " -> Свободно" : " -> Занято") : "")
                                    : slot.getStaffModel().getProfession().getProfession_type() + " -> "
                                    + slot.getDate().getDayOfMonth() + " "
                                    + MonthEnum.getByNumber(slot.getDate().getMonthValue()).getMonthGenitive() + " " + slot.getTime() + "\n";
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

    public InlineKeyboardMarkup createMonthDayKeyboard(
            YearMonth yearMonth,
            String prefix
    ) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        LocalDate today = LocalDate.now();
        boolean isCurrentMonth =
                today.getYear() == yearMonth.getYear()
                        && today.getMonthValue() == yearMonth.getMonthValue();

        int daysInMonth = yearMonth.lengthOfMonth();
        int day = 1;

        while (day <= daysInMonth) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            boolean hasRealDay = false;
            for (int j = 0; j < 8; j++) {
                InlineKeyboardButton button;
                if (day <= daysInMonth) {
                    hasRealDay = true;
                    String text = String.valueOf(day);

                    if (isCurrentMonth && day == today.getDayOfMonth()) {
                        text = "•" + day + "• ";
                    }

                    button = InlineKeyboardButton.builder()
                            .text(text + " ")
                            .callbackData(prefix + ":" + day)
                            .build();
                    day++;
                } else {
                    button = InlineKeyboardButton.builder()
                            .text(" ")
                            .callbackData("empty")
                            .build();
                }
                row.add(button);
            }
            if (hasRealDay) {
                rows.add(row);
            }
        }
        return new InlineKeyboardMarkup(rows);
    }

    public InlineKeyboardMarkup createGridKeyboard(
            List<String> items,
            String prefix,
            int columns,
            String selectedItem
    ) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        int index = 0;
        while (index < items.size()) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            for (int col = 0; col < columns; col++) {
                InlineKeyboardButton button;
                if (index < items.size()) {
                    String item = items.get(index);

                    String text = item;
                    if (selectedItem != null && selectedItem.equals(item)) {
                        text = "•" + item + "• ";
                    }

                    button = InlineKeyboardButton.builder()
                            .text(text + " ")
                            .callbackData(prefix + ":" + item)
                            .build();
                    index++;
                } else {
                    button = InlineKeyboardButton.builder()
                            .text(" ")
                            .callbackData("empty")
                            .build();
                }

                row.add(button);
            }
            rows.add(row);
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