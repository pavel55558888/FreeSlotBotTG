package example.ru.freeslotbottg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HelpEnums {
    HELP_MESSAGE("""
        <b>В случае плохой работы бота необходимо убедиться, что у вашего аккаунта есть username</b>
        """);

    private final String template;
}
