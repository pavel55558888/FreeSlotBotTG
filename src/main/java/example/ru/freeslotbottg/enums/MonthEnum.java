package example.ru.freeslotbottg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum MonthEnum {
    JANUARY(1, "Января"),
    FEBRUARY(2, "Февраля"),
    MARCH(3, "Марта"),
    APRIL(4, "Апреля"),
    MAY(5, "Мая"),
    JUNE(6, "Июня"),
    JULY(7, "Июля"),
    AUGUST(8, "Августа"),
    SEPTEMBER(9, "Сентября"),
    OCTOBER(10, "Октября"),
    NOVEMBER(11, "Ноября"),
    DECEMBER(12, "Декабря");

    private final int number;
    private final String month;

    public static MonthEnum getByNumber(int number) {
        return Arrays.stream(values())
                .filter(month -> month.number == number)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неверный номер месяца: " + number));
    }

    public static MonthEnum getByMonthName(String monthName) {
        return Arrays.stream(values())
                .filter(month -> month.month.equalsIgnoreCase(monthName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный месяц: " + monthName));
    }
}
