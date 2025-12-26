package example.ru.freeslotbottg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageHandlerEnum {
    CHOICE_PROFESSION("Выберите вид деятельности:"),
    NULL_SLOTS("У вас нет записей."),
    CHOICE_CANCEL("Выберите запись для отмены:"),
    COMMAND_NOT_FOUND("Данной команды не существует"),
    NEW_PROFESSION("Новый вид деятельности успешно добавлен\n"),
    NEW_MASTER("Новый мастер успешно добавлен"),
    NOT_FOUND_ADMIN_NEW_MASTER("Неизвестная ошибка: формат /admin/new/master/профессия/username/firstname/lastname"),
    MASTER_NULL("Мастер не найден"),
    DELETE_PROFESSION("Вид деятельности успешно удален\n"),
    DELETE_MASTER("Мастер успешно удален"),
    YOUR_SLOT("Ваши слоты:"),
    VALIDATE_NEW_SLOT("Используйте: /slot/YYYY-MM-DD/HH:mm"),
    DATE_NOT_PAST("Дата не может быть в прошлом"),
    ADD_NEW_SLOT("Новый слот успешно добавлен");
    private final String template;
}
