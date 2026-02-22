package example.ru.freeslotbottg.service;

import example.ru.freeslotbottg.cache.SlotCache;
import example.ru.freeslotbottg.cache.model.SlotCacheModel;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.service.slots.DeleteSlotById;
import example.ru.freeslotbottg.database.service.slots.GetSlotById;
import example.ru.freeslotbottg.database.service.slots.SetSlot;
import example.ru.freeslotbottg.database.service.staff.GetStaffByUsername;
import example.ru.freeslotbottg.enums.MessageAndCallbackEnum;
import example.ru.freeslotbottg.enums.MonthEnum;
import example.ru.freeslotbottg.service.pagination.CallbackHandlerMasterPaginationService;
import example.ru.freeslotbottg.util.BuilderMessage;
import example.ru.freeslotbottg.util.KeyboardFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CallbackHandlerMasterService {
    private final GetSlotById getSlotById;
    private final DeleteSlotById deleteSlotById;
    private final CallbackHandlerMasterPaginationService callbackHandlerMasterPaginationService;
    private final KeyboardFactory keyboardFactory;
    private final SlotCache slotCache;
    private final BuilderMessage builderMessage;
    private final GetStaffByUsername getStaffByUsername;
    private final SetSlot setSlots;

    public List<BotApiMethod<?>> caseDelete(List<BotApiMethod<?>> actions, long chatId,
                                            int messageId, String value, int page, String prefix) {

        if (!(page < 0)){
            return callbackHandlerMasterPaginationService.caseDeletePagination(actions, chatId, messageId, value, page, prefix);
        }

        Optional<SlotModel> slotOpt = Optional.ofNullable(getSlotById.getSlotById(Long.parseLong(value)));

        if (slotOpt.isEmpty()) {
            actions.add(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(MessageAndCallbackEnum.ERROR_FORBIDDEN.getTemplate())
                    .build());
            return actions;
        }

        SlotModel slot = slotOpt.get();

        if (!slot.isAvailable()) {
            String messageText = MessageAndCallbackEnum.SLOT_DELETED_NOTIFY.format(Map.of(
                    "master", slot.getStaffModel().toString(),
                    "date", slot.getDate().toString(),
                    "time", slot.getTime().toString()
            ));

            actions.add(SendMessage.builder()
                    .chatId(slot.getChatId())
                    .text(messageText)
                    .parseMode("HTML")
                    .build());
        }

        deleteSlotById.deleteSlot(Long.parseLong(value));

        actions.add(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(MessageAndCallbackEnum.SLOT_DELETED_SUCCESS.getTemplate())
                .build());

        return actions;
    }

    public List<BotApiMethod<?>> caseCheckSlot(List<BotApiMethod<?>> actions, long chatId, int messageId,
                                               String value, int page, String prefix) {

        if (!(page < 0)){
            return callbackHandlerMasterPaginationService.caseCheckSlotsPagination(actions, chatId, messageId, value, page, prefix);
        }

        Optional<SlotModel> slotOpt = Optional.ofNullable(getSlotById.getSlotById(Long.parseLong(value)));

        if (slotOpt.isEmpty()) {
            actions.add(SendMessage.builder()
                    .chatId(chatId)
                    .text(MessageAndCallbackEnum.SLOT_NOT_FOUND.getTemplate())
                    .parseMode("HTML")
                    .build());
            return actions;
        }

        SlotModel slot = slotOpt.get();

        if (!slot.isAvailable()) {
            actions.add(SendMessage.builder()
                    .chatId(chatId)
                    .text(MessageAndCallbackEnum.SLOT_INFO_TAKEN.format(Map.of(
                            "date", slot.getDate().toString(),
                            "time", slot.getTime().toString(),
                            "clientName", slot.getFirstNameClient() + " " + slot.getLastNameClient(),
                            "usernameClient", slot.getUsernameClient() != null ? slot.getUsernameClient() : "—"
                    )))
                    .parseMode("HTML")
                    .build());
        } else {
            Map<String, String> freeSlotReplacements = Map.of(
                    "date", slot.getDate().toString(),
                    "time", slot.getTime().toString()
            );
            actions.add(SendMessage.builder()
                    .chatId(chatId)
                    .text(MessageAndCallbackEnum.SLOT_INFO_AVAILABLE.format(freeSlotReplacements))
                    .parseMode("HTML")
                    .build());
        }

        return actions;
    }

    public List<BotApiMethod<?>> caseSetSlotGetDays(List<BotApiMethod<?>> actions, long chatId, int messageId, String value, String username) {
        actions.add(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(MessageAndCallbackEnum.YOU_SELECTED_MONTH.format(
                        Map.of("value", value)))
                .parseMode("HTML")
                .build());

        int year = LocalDate.now().getYear()
                + (MonthEnum.getByMonthName(value).getNumber() < LocalDate.now().getMonthValue() ? 1 : 0);;
        actions.add(SendMessage.builder()
                .chatId(chatId)
                .text(MessageAndCallbackEnum.CHOOSE_DAY.getTemplate())
                .replyMarkup(keyboardFactory
                        .createMonthDayKeyboard(
                                YearMonth.of(
                                        LocalDate.now().getYear()
                                        + (MonthEnum.getByMonthName(value).getNumber() < LocalDate.now().getMonthValue() ? 1 : 0)
                                        , MonthEnum.getByMonthName(value).getNumber())
                                ,"setSlotDay")
                )
                .build());

        slotCache.setCache(username, new SlotCacheModel(year, MonthEnum.getByMonthName(value).getNumber(), null,
                null, null, System.currentTimeMillis()));
        return actions;
    }

    public List<BotApiMethod<?>> caseSetSlotGetHours(List<BotApiMethod<?>> actions, long chatId, int messageId, String value, String username) {
        Optional<SlotCacheModel> slotCacheModel = slotCache.getCache(username);

        if (slotCacheModel.isEmpty()) {
            actions.add(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(MessageAndCallbackEnum.CACHE_EXPIRED.getTemplate())
                    .parseMode("HTML")
                    .build());
            return actions;
        }

        slotCacheModel.get().setDay(Integer.valueOf(value));
        slotCache.setCache(username, slotCacheModel.get());

        actions.add(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(MessageAndCallbackEnum.YOU_SELECTED_DAY.format(
                        Map.of("value", value)))
                .parseMode("HTML")
                .build());

        List<String> hoursList = IntStream.range(0, 24)
                .mapToObj(h -> String.format("%02d", h))
                .toList();
        actions.add(SendMessage.builder()
                .chatId(chatId)
                .text(MessageAndCallbackEnum.CHOOSE_HOURS.getTemplate())
                .replyMarkup(keyboardFactory
                        .createGridKeyboard(
                                hoursList,
                                "setSlotHours",
                                6,
                                null
                        ))
                .build());
        return actions;
    }

    public List<BotApiMethod<?>> caseSetSlotGetHoursAndTimes(List<BotApiMethod<?>> actions, long chatId, int messageId, String value, String username) {
        Optional<SlotCacheModel> slotCacheModel = slotCache.getCache(username);

        if (slotCacheModel.isEmpty()) {
            actions.add(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(MessageAndCallbackEnum.CACHE_EXPIRED.getTemplate())
                    .parseMode("HTML")
                    .build());
            return actions;
        }

        slotCacheModel.get().setHour(Integer.parseInt(value));
        slotCache.setCache(username, slotCacheModel.get());

        List<String> times = IntStream.range(0, 6)
                .mapToObj(i -> String.format("%02d:%02d", Integer.parseInt(value), i * 10))
                .toList();
        actions.add(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(MessageAndCallbackEnum.CHOOSE_MINUTES.getTemplate())
                .replyMarkup(keyboardFactory
                        .createGridKeyboard(
                                times,
                                "setSlotMinute",
                                2,
                                null
                        ))
                .build());
        return actions;
    }

    public List<BotApiMethod<?>> caseSetSlotFinale(List<BotApiMethod<?>> actions, long chatId, int messageId,
                                                   String hour, String time, String username) {
        Optional<SlotCacheModel> slotCacheModel = slotCache.getCache(username);

        if (slotCacheModel.isEmpty()) {
            actions.add(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(MessageAndCallbackEnum.CACHE_EXPIRED.getTemplate())
                    .parseMode("HTML")
                    .build());
            return actions;
        }

        slotCacheModel.get().setMinute(Integer.parseInt(time));
        slotCache.setCache(username, slotCacheModel.get());

        actions.add(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(MessageAndCallbackEnum.YOU_SELECTED_TIME.format(
                        Map.of("time", hour + ":" + time)))
                .parseMode("HTML")
                .build());

        Optional<StaffModel> staffModel = getStaffByUsername.getStaffByUsername(username);

        if (staffModel.isEmpty()) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
        }

        if (slotCacheModel.get().objectIsNull()){
            log.error("Error: SlotCache is empty: " + slotCacheModel);
            actions.add(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(MessageAndCallbackEnum.UNEXPECTED_ERROR.getTemplate())
                    .build());
            return actions;
        }

        try {
            LocalDate localDate = LocalDate.of(
                    slotCacheModel.get().getYear(),
                    slotCacheModel.get().getMonth(),
                    slotCacheModel.get().getDay()
            );
            LocalTime localTime = LocalTime.of(slotCacheModel.get().getHour(), slotCacheModel.get().getMinute());

            if (localDate.isBefore(LocalDate.now())) {
                return builderMessage.buildMessage(MessageAndCallbackEnum.DATE_NOT_PAST.getTemplate(), chatId);
            }

            setSlots.setSlots(new SlotModel(staffModel.get(), localDate, localTime));

        } catch (Exception e) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.VALIDATE_NEW_SLOT.getTemplate(), chatId);
        }

        actions.add(SendMessage.builder()
                .chatId(chatId)
                .text(MessageAndCallbackEnum.ADD_NEW_SLOT.getTemplate())
                .parseMode("HTML")
                .build());

        slotCache.removeCache(username);

        return actions;
    }

}
