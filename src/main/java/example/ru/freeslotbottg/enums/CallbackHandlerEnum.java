package example.ru.freeslotbottg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@AllArgsConstructor
public enum CallbackHandlerEnum {
    ERROR_FORBIDDEN("Ошибка сервера: запись не найдена"),
    SLOT_DELETED_SUCCESS("Слот успешно удален"),
    SLOT_DELETED_NOTIFY("<b>Слот на который вы были записаны удалили, выберите новый слот, пожалуйста.</b>\n\n" +
            "👤 Мастер: <b>${master}</b>\n" +
            "📅 Дата: <b>${date}</b>\n" +
            "⏰ Время: <b>${time}</b>\n"),
    SLOT_NOT_FOUND("Ошибка: такого слота не существует"),
    SLOT_INFO_TAKEN("<b>Подробная информация о слоте:</b>\n\n" +
            "📅 Дата: <b>${date}</b>\n" +
            "⏰ Время: <b>${time}</b>\n" +
            "👤 Клиент: <b>${clientName}</b>\n" +
            "🔖 Telegram: @<b>${usernameClient}</b>\n"),

    SLOT_INFO_AVAILABLE("<b>Свободный слот:</b> \n\n" +
            "📅 Дата: <b>${date}</b>\n" +
            "⏰ Время: <b>${time}</b>\n"),
    NO_MASTERS_IN_CATEGORY("Нет доступных мастеров в этой категории."),
    NO_SLOTS_FOR_MASTER("Нет свободных слотов у этого мастера."),
    SUCCESS_BOOKING(
            "✅ <b>Вы успешно записаны!</b>\n\n" +
                    "👤 Мастер: <b>${master}</b>\n" +
                    "📅 Дата: <b>${date}</b>\n" +
                    "⏰ Время: <b>${time}</b>\n" +
                    "За день до записи отправим вам уведомление"
    ),
    NOTIFICATION_TO_MASTER_NEW_BOOKING(
            "🔔 <b>К вам новая запись!</b>\n\n" +
                    "👤 Клиент: <b>${clientName}</b>\n" +
                    "🔖 Telegram: <b>@${username}</b>\n" +
                    "📅 Дата: <b>${date}</b>\n" +
                    "⏰ Время: <b>${time}</b>\n"
    ),
    SLOT_ALREADY_TAKEN("Кажется у вас из подноса украли запись 🤪 \n Попробуйте выбрать другое время"),
    CANCELLATION_SUCCESS(
            "<b>Ваша запись успешно отменена:</b>\n\n" +
                    "👤 Мастер: <b>${master}</b>\n" +
                    "📅 Дата: <b>${date}</b>\n" +
                    "⏰ Время: <b>${time}</b>\n"
    ),
    NOTIFICATION_TO_MASTER_CANCELLATION(
            "<b>Клиент отменил запись:</b>\n\n" +
                    "📅 Дата: <b>${date}</b>\n" +
                    "⏰ Время: <b>${time}</b>\n" +
                    "👤 Клиент: <b>${clientName}</b>\n" +
                    "🔖 Telegram: @<b>${usernameClient}</b>\n"
    ),
    YOU_SELECTED("Вы выбрали: <b>${value}</b>"),
    CHOOSE_MASTER("Выберите мастера:"),
    CHOOSE_SLOT("Выберите слот:");

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
