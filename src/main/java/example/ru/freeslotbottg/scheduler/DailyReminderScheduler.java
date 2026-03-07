package example.ru.freeslotbottg.scheduler;

import example.ru.freeslotbottg.bot.TelegramBot;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.service.slots.DeleteSlotById;
import example.ru.freeslotbottg.database.service.slots.GetAllSlots;
import example.ru.freeslotbottg.database.service.slots.UpdateSlot;
import example.ru.freeslotbottg.enums.MonthEnum;
import example.ru.freeslotbottg.enums.SchedulerNotifyEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyReminderScheduler {
    private final GetAllSlots getAllSlots;
    private final TelegramBot telegramBot;
    private final DeleteSlotById deleteSlotById;
    private final UpdateSlot updateSlot;

    @Value("${batch.size.db.reminder.client}")
    private  int batchSize;

    @Scheduled(cron = "${scheduler.reminder.crone}")
    private void sendDailyReminder() {
        log.info("Запуск ежедневного напоминания");

        int page = 0;
        List<SlotModel> slots;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeMonthsAgo = now.minusMonths(3);

        do {
            slots = getAllSlots.getSlots(true, page, batchSize);

            for (SlotModel slot : slots) {
                LocalDateTime slotDateTime = LocalDateTime.of(slot.getDate(), slot.getTime());

                // Если запись уже прошла
                if (slotDateTime.isBefore(now)) {
                    // если прошло 3 месяца после записи
                    if (slotDateTime.isBefore(threeMonthsAgo)) {
                        deleteSlotById.deleteSlot(slot.getId());
                    }
                } else {
                    pushReminder(slotDateTime, now, slot);
                }
            }
            page++;
        } while (!slots.isEmpty());
    }
    public void pushReminder(LocalDateTime slotDateTime,  LocalDateTime now, SlotModel slot) {
        //Если запись ещё не началась И она завтра И она зарезервирована И уведомление еще не отправлено
        if (slotDateTime.isAfter(now)
                && ChronoUnit.DAYS.between(now.toLocalDate(), slot.getDate()) == 1
                && !slot.isAvailable()
                && !slot.isPushNotify()) {
            try {

                telegramBot.execute(SendMessage.builder()
                        .chatId(slot.getClient().getChatId())
                        .text(SchedulerNotifyEnum.NOTIFY_USER.format(Map.of(
                                "profession", slot.getStaff().getProfession().getProfession_type(),
                                "masterFullName", slot.getStaff().getFirstName() + " "
                                        + slot.getStaff().getLastName(),
                                "date", slot.getDate().getDayOfMonth() + " " + MonthEnum.getByNumber(slot.getDate().getMonthValue()).getMonthGenitive(),
                                "time", slot.getTime().toString()
                        )))
                        .parseMode("HTML")
                        .build());


                telegramBot.execute(SendMessage.builder()
                        .chatId(slot.getStaff().getChatId())
                        .text(SchedulerNotifyEnum.NOTIFY_MASTER.format(Map.of(
                                "profession", slot.getStaff().getProfession().getProfession_type(),
                                "masterFullName", slot.getStaff().getFirstName() + " "
                                        + slot.getStaff().getLastName(),
                                "date", slot.getDate().getDayOfMonth() + " " + MonthEnum.getByNumber(slot.getDate().getMonthValue()).getMonthGenitive(),
                                "time", slot.getTime().toString(),
                                "clientFullName", slot.getClient().getFirstName() + " " + slot.getClient().getLastName(),
                                "telegram", slot.getClient().getUsername()
                        )))
                        .parseMode("HTML")
                        .build());


                slot.setPushNotify(true);
                updateSlot.updateSlot(slot);
            } catch (TelegramApiException ex) {
                log.error("Ошибка при отправке напоминания пользователю: \n" + ex.getMessage());
            }
        }
    }
}
