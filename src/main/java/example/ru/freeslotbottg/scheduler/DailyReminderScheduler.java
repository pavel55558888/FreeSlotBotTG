package example.ru.freeslotbottg.scheduler;

import example.ru.freeslotbottg.bot.TelegramBot;
import example.ru.freeslotbottg.cache.UserStateCache;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.service.slots.DeleteSlotById;
import example.ru.freeslotbottg.database.service.slots.GetAllSlots;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyReminderScheduler {
    private final GetAllSlots getAllSlots;
    private final TelegramBot telegramBot;
    private final DeleteSlotById deleteSlotById;
    private final UserStateCache userStateCache;

    @Scheduled(cron = "0 00 11 * * ?")
    private void sendDailyReminders() {
        log.info("Запуск ежедневного уведомления");
        List<SlotModel> allSlots = getAllSlots.getSlots();
        LocalDateTime now = LocalDateTime.now();
        allSlots.forEach(slot -> {
            LocalDateTime slotDateTime = LocalDateTime.of(slot.getDate(), slot.getTime());

            //Если запись уже прошла
            if (slotDateTime.isBefore(now)) {
                deleteSlotById.deleteSlot(slot.getId());
            }else {
                //Если запись ещё не началась И до она завтра И она зарезервирована
                if (slotDateTime.isAfter(now) &&
                        ChronoUnit.DAYS.between(now.toLocalDate(), slot.getDate()) == 1 && !slot.isAvailable()) {
                    try {
                        telegramBot.execute(SendMessage.builder()
                                .chatId(slot.getChatId())
                                .text("Привет! Напоминаем о записи.\n" +
                                        slot.getStaffModel().getProfession().getProfession_type() + "\n" +
                                        "Мастер: <b>" + slot.getStaffModel() + "</b>\n" +
                                        "Дата и время: <b>" + slot.getDate() + " " + slot.getTime() + "</b>")
                                .parseMode("HTML")
                                .build());
                    } catch (Exception ex) {
                        log.error("Ошибка при отправке уведомления пользователю: \n" + ex.getMessage());
                    }
                }
            }
        });
    }
}
