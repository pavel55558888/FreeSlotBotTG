package example.ru.freeslotbottg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum MonthEnum {
    JANUARY(1, "Января", "Январь"),
    FEBRUARY(2, "Февраля", "Февраль"),
    MARCH(3, "Марта", "Март"),
    APRIL(4, "Апреля", "Апрель"),
    MAY(5, "Мая", "Май"),
    JUNE(6, "Июня", "Июнь"),
    JULY(7, "Июля", "Июль"),
    AUGUST(8, "Августа", "Август"),
    SEPTEMBER(9, "Сентября", "Сентябрь"),
    OCTOBER(10, "Октября", "Октябрь"),
    NOVEMBER(11, "Ноября", "Ноябрь"),
    DECEMBER(12, "Декабря", "Декабрь");


    private final int number;
    private final String monthGenitive;
    private final String monthNominative;

    public static MonthEnum getByNumber(int number) {
        return Arrays.stream(values())
                .filter(month -> month.number == number)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неверный номер месяца: " + number));
    }

    public static MonthEnum getByMonthName(String monthName) {
        return Arrays.stream(values())
                .filter(month -> month.getMonthGenitive().equalsIgnoreCase(monthName)
                        || month.getMonthNominative().equalsIgnoreCase(monthName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный месяц: " + monthName));
    }


    public static List<String> getMonthNominativeNames() {
        return Arrays.stream(values())
                .map(MonthEnum::getMonthNominative)
                .toList();
    }
}
