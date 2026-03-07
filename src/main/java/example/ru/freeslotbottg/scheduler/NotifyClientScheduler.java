package example.ru.freeslotbottg.scheduler;

import example.ru.freeslotbottg.bot.TelegramBot;
import example.ru.freeslotbottg.cache.NotifyQueueCache;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.service.client.UpdateClient;
import example.ru.freeslotbottg.database.service.slots.GetAllSlotsByStaff;
import example.ru.freeslotbottg.enums.SchedulerNotifyEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotifyClientScheduler {
    private final TelegramBot telegramBot;
    private final NotifyQueueCache notifyQueueCache;
    private final GetAllSlotsByStaff getAllSlotsByStaff;
    private final UpdateClient updateClient;

    @Value("${batch.size.db.notify.client}")
    private int batchSize;

    @Scheduled(cron = "${scheduler.notify.crone}")
    private void sendDailyNotify() {
        log.info("Запуск уведомления о новом слоте");
        Deque<StaffModel> slotQueue = notifyQueueCache.getAllQueue();
        notifyQueueCache.clearCache();

        slotQueue.forEach(staff -> {
            int page = 0;
            List<SlotModel> slots;

            do {
                slots = getAllSlotsByStaff.getAllSlotsByStaff(
                        staff,
                        false,
                        true,
                        page,
                        batchSize,
                        false
                );

                List<SlotModel> uniqueSlots = new ArrayList<>(
                        slots.stream()
                                .filter(slot -> slot.getClient() != null)
                                .collect(Collectors.toMap(
                                        slot -> slot.getClient().getId(),
                                        slot -> slot,
                                        (existing, replacement) -> existing
                                ))
                                .values()
                );

                for (SlotModel slot : uniqueSlots) {

                    if (slot.getClient() == null) continue;
                    LocalDate lastPush = slot.getClient().getLastPushNotify();
                    LocalDate threeDaysAgo = LocalDate.now().minusDays(3);

                    if (lastPush == null || !lastPush.isAfter(threeDaysAgo)) {
                        pushNotify(slot);
                    }
                }
                page++;
            } while (!slots.isEmpty());
        });
    }

    public void pushNotify(SlotModel slot) {
        try {
            telegramBot.execute(SendMessage.builder()
                    .chatId(slot.getClient().getChatId())
                    .text(SchedulerNotifyEnum.NOTIFY_USER_NEW_SLOT.format(
                            Map.of(
                                    "profession", slot.getStaff().getProfession().getProfession_type(),
                                    "masterFullName", slot.getStaff().getFirstName() + " "
                                            + slot.getStaff().getLastName()
                            )))
                    .parseMode("HTML")
                    .build());

            slot.getClient().setLastPushNotify(LocalDate.now());
            updateClient.updateClient(slot.getClient());
        } catch (TelegramApiException ex) {
            log.error("Ошибка при отправке уведомления пользователю: \n" + ex.getMessage());
        }
    }
}
