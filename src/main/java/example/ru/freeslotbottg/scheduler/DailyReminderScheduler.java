package example.ru.freeslotbottg.scheduler;

import example.ru.freeslotbottg.bot.TelegramBot;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.service.slots.DeleteSlotById;
import example.ru.freeslotbottg.database.service.slots.GetAllSlotsNotAvailable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class DailyReminderScheduler {
    private final GetAllSlotsNotAvailable getAllSlotsNotAvailable;
    private final TelegramBot telegramBot;
    private final DeleteSlotById deleteSlotById;

    @Scheduled(cron = "0 0 12 * * ?")
    public void sendDailyReminders() {
        List<SlotModel> allSlots = getAllSlotsNotAvailable.getSlotsNotAvailable();
        allSlots.forEach(slot -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime slotDateTime = LocalDateTime.of(slot.getDate(), slot.getTime());

            //Если запись уже прошла
            if (slotDateTime.isBefore(now)) {
                deleteSlotById.deleteSlot(slot.getId());
                allSlots.remove(slot);
            }

            //Если запись ещё не началась И до неё остался ровно 1 день (± несколько часов) И она зарезервирована
            if (slotDateTime.isAfter(now) &&
                    ChronoUnit.DAYS.between(now.toLocalDate(), slot.getDate()) == 1) {
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
        });
    }
}
