package example.ru.freeslotbottg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HelpEnums {
    HELP_MESSAGE("""
        ⚠️ <b>В случае проблем с ботом убедитесь, что у вашего telegram аккаунта есть username 🆔</b>
        """);

    private final String template;
}
