package example.ru.freeslotbottg.scheduler;

import example.ru.freeslotbottg.bot.TelegramBot;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.service.slots.DeleteSlotById;
import example.ru.freeslotbottg.database.service.slots.GetAllSlots;
import example.ru.freeslotbottg.database.service.slots.UpdateSlot;
import example.ru.freeslotbottg.enums.SchedulerNotifyEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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

    @Scheduled(cron = "${scheduler.notify.crone}")
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

                //Если запись ещё не началась И она завтра И она зарезервирована И уведомление еще не отправлено
                if (slotDateTime.isAfter(now)
                        && ChronoUnit.DAYS.between(now.toLocalDate(), slot.getDate()) == 1
                        && !slot.isAvailable()
                        && !slot.isPushNotify()) {
                    try {

                        telegramBot.execute(SendMessage.builder()
                                .chatId(slot.getChatId())
                                .text(SchedulerNotifyEnum.NOTIFY_USER.format(Map.of(
                                        "profession", slot.getStaffModel().getProfession().getProfession_type(),
                                        "masterFullName", slot.getStaffModel().getFirstName() + " "
                                                + slot.getStaffModel().getLastName(),
                                        "date", slot.getDate().toString(),
                                        "time", slot.getTime().toString()
                                )))
                                .parseMode("HTML")
                                .build());


                        telegramBot.execute(SendMessage.builder()
                                .chatId(slot.getStaffModel().getChatId())
                                .text(SchedulerNotifyEnum.NOTIFY_MASTER.format(Map.of(
                                        "profession", slot.getStaffModel().getProfession().getProfession_type(),
                                        "masterFullName", slot.getStaffModel().getFirstName() + " "
                                                + slot.getStaffModel().getLastName(),
                                        "date", slot.getDate().toString(),
                                        "time", slot.getTime().toString(),
                                        "clientFullName", slot.getFirstNameClient() + " " + slot.getLastNameClient(),
                                        "telegram", slot.getUsernameClient()
                                )))
                                .parseMode("HTML")
                                .build());


                        slot.setPushNotify(true);
                        updateSlot.updateSlot(slot);
                    } catch (Exception ex) {
                        log.error("Ошибка при отправке уведомления пользователю: \n" + ex.getMessage());
                    }
                }
            }
        });
    }
}
