package example.ru.freeslotbottg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StartEnums {
    START_MESSAGE("""
        \uD83D\uDC4B Привет, %s!

        Это сервис онлайн-записи к специалистам.
        Выберите мастера, удобное время — и подтвердите запись.

        Готовы?
        """);

    private final String template;

    public String format(Object... args) {
        return String.format(template, args);
    }
}
