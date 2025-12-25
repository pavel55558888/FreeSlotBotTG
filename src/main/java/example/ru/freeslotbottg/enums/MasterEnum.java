package example.ru.freeslotbottg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MasterEnum {
    HI_MASTER("""
            👋 Добро пожаловать, мастер!\n
            Вы успешно добавлены в систему записи.\n
            Удачи в работе! 💼
    """),
    MASTER_INFO("""
            Полезные команды:
            \n
            • <b>Добавлять свободные слоты</b>\n
              Пример: <code>/slot/2025-12-31/15:00</code>
            \n
            • <b>Удалять лишние слоты</b>\n
              Пример: <code>/slot/delete</code>
            \n
            • <b>Управлять своими записями</b>\n
              Просмотр всех слотов: <code>/slot/all</code>
            \n
            """);
    private final String template;
}
