package example.ru.freeslotbottg.scheduler;

import example.ru.freeslotbottg.bot.TelegramBot;
import example.ru.freeslotbottg.cache.JobEvaluationCache;
import example.ru.freeslotbottg.cache.model.JobEvaluationCacheModel;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.service.slots.GetAllSlots;
import example.ru.freeslotbottg.database.service.slots.UpdateSlot;
import example.ru.freeslotbottg.enums.MessageAndCallbackEnum;
import example.ru.freeslotbottg.enums.SchedulerJobEvaluation;
import example.ru.freeslotbottg.enums.SchedulerNotifyEnum;
import example.ru.freeslotbottg.util.KeyboardFactory;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyJobEvaluationScheduler {
    private final GetAllSlots getAllSlots;
    private final TelegramBot telegramBot;
    private final UpdateSlot updateSlot;
    private final KeyboardFactory keyboardFactory;
    private final JobEvaluationCache jobEvaluationCache;

    @Value("${batch.size.db.job.evaluation.client}")
    private  int batchSize;

    private InlineKeyboardMarkup inlineKeyboardMarkup;

    @PostConstruct
    public void init() {
        List<String> jobEvaluationList = List.of(
                SchedulerJobEvaluation.ONE.getTemplate(),
                SchedulerJobEvaluation.TWO.getTemplate(),
                SchedulerJobEvaluation.THREE.getTemplate(),
                SchedulerJobEvaluation.FOUR.getTemplate(),
                SchedulerJobEvaluation.FIVE.getTemplate()
        );
        inlineKeyboardMarkup = keyboardFactory.createKeyboard(jobEvaluationList, "jobEvaluation", 0, false);
    }

    @Scheduled(cron = "${scheduler.job.evaluation.crone}")
    private void sendDailyJobEvaluation() {
        log.info("Запуск оценки работы");

        int page = 0;
        List<SlotModel> slots;
        LocalDateTime now = LocalDateTime.now();

        do {
            slots = getAllSlots.getSlots(true, page, batchSize);

            for (SlotModel slot : slots) {
                LocalDateTime slotDateTime = LocalDateTime.of(slot.getDate(), slot.getTime());
                LocalDateTime fourHoursAfterSlot = slotDateTime.plusHours(4);
                LocalDateTime fortyEightHoursAfter = slotDateTime.plusDays(2);

                if (now.isAfter(fourHoursAfterSlot) && now.isBefore(fortyEightHoursAfter) && !slot.isPushJobEvaluation()) {
                    // прошло 4 часа и не более 2ух дней
                    pushJobEvaluation(slot);
                }
            }
            page++;
        } while (!slots.isEmpty());
    }
    public void pushJobEvaluation(SlotModel slot) {
        try {
            jobEvaluationCache.setCache(slot.getClient().getUsername(), new JobEvaluationCacheModel(slot, System.currentTimeMillis()));

            telegramBot.execute(SendMessage.builder()
                    .chatId(slot.getClient().getChatId())
                    .text(SchedulerJobEvaluation.USER_FEEDBACK.getTemplate())
                    .parseMode("HTML")
                    .replyMarkup(inlineKeyboardMarkup)
                    .build());

            slot.setPushJobEvaluation(true);
            updateSlot.updateSlot(slot);
        } catch (TelegramApiException ex) {
            log.error("Ошибка при отправке напоминания пользователю: \n" + ex.getMessage());
        }
    }
}
