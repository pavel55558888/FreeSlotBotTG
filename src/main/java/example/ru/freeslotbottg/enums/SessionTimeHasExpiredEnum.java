package example.ru.freeslotbottg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SessionTimeHasExpiredEnum {
    SESSION_TIME_HAS_EXPIRED("Время жизни вашего сеанса истекло, начните сначала.");
    private final String template;;
}
