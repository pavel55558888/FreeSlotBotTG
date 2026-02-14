package example.ru.freeslotbottg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Getter
public enum SchedulerNotifyEnum {

    NOTIFY_USER(
            "\uD83D\uDC4B Привет! Напоминаем о записи.\n" +
                    "🎭 Вид деятельности: <b>${profession}</b>\n" +
                    "👤 Мастер: <b>${masterFullName}</b>\n" +
                    "📅 Дата: <b>${date}</b>\n" +
                    "⏰ Время: <b>${time}</b>"
    ),
    NOTIFY_MASTER(
            "\uD83D\uDC4B Привет, мастер! Напоминаем о записи.\n" +
                    "🎭 Вид деятельности: <b>${profession}</b>\n" +
                    "👤 Мастер: <b>${masterFullName}</b>\n" +
                    "📅 Дата: <b>${date}</b>\n" +
                    "⏰ Время: <b>${time}</b>\n" +
                    "👤 Клиент: <b>${clientFullName}</b>\n" +
                    "🔖 Telegram: <b>@${telegram}</b>");


    private final String template;

    public String format(Map<String, String> replacements) {
        String result = this.template;
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(result);
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = replacements.getOrDefault(key, matcher.group(0));
            result = result.replace("${" + key + "}", replacement);
        }
        return result;
    }
}
