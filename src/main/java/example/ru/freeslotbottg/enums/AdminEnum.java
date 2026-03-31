package example.ru.freeslotbottg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminEnum {
    ADMIN_INFO("""
            Полезные команды:
            \n
            • <b>Добавить нового мастера, если такого вида деятельности не найдено, то создается новый</b>\n
              Пример: <code>/admin/new/master/разработчик/@QvSwmuNxc/павел/викулин</code>
            \n
            • <b>Удалить мастера</b>\n
              Пример: <code>/admin/delete/master/@QvSwmuNxc</code>
            \n
            • <b>Разослать уведомления всем пользователям, которые были активны не более 3 месяцев назад</b>\n
              Пример: <code>/admin/send/notify/Всем привет!</code>\n
              Поддерживает теги &lt;b&gt; и \\n
            \n
            """);
    private String template;
}
