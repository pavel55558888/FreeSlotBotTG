package example.ru.freeslotbottg.service;

import example.ru.freeslotbottg.cache.UserStateCache;
import example.ru.freeslotbottg.cache.model.UserStateCacheModel;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.service.slots.GetAllSlotsByStaff;
import example.ru.freeslotbottg.database.service.slots.SetSlot;
import example.ru.freeslotbottg.database.service.staff.*;
import example.ru.freeslotbottg.enums.MasterEnum;
import example.ru.freeslotbottg.enums.MessageAndCallbackEnum;
import example.ru.freeslotbottg.enums.MonthEnum;
import example.ru.freeslotbottg.enums.Pagination;
import example.ru.freeslotbottg.util.BuilderMessage;
import example.ru.freeslotbottg.util.KeyboardFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageHandlerMasterService {
    private final KeyboardFactory keyboardFactory;
    private final SetSlot setSlot;
    private final GetStaffByUsername getStaffByUsername;
    private final UpdateStaff updateStaff;
    private final GetAllSlotsByStaff getAllSlotsByStaff;
    private final BuilderMessage builderMessage;
    private final UserStateCache userStateCache;

    public List<BotApiMethod<?>> caseCheckAllSlots(long chatId, String username) {
        log.info("Command /slot/all");
        Optional<StaffModel> staffModel = getStaffByUsername.getStaffByUsername(username);

        if (staffModel.isEmpty()) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
        }

        List<SlotModel> slotModel = getAllSlotsByStaff.getAllSlotsByStaff(
                staffModel.get(),
                null,
                true,
                Pagination.START_INDEX_PAGE.getTemplate(),
                Pagination.PAGE_SIZE.getTemplate()
        );

        if (slotModel.isEmpty()) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.NULL_SLOTS.getTemplate(), chatId);
        }

        userStateCache.setCache(chatId, new UserStateCacheModel(username, System.currentTimeMillis()));
        SendMessage choice = SendMessage.builder()
                .chatId(chatId)
                .text(MessageAndCallbackEnum.YOUR_SLOT.getTemplate())
                .replyMarkup(keyboardFactory.buildSlotKeyboard(slotModel, "check_slot", Pagination.START_INDEX_PAGE.getTemplate(), true, true))
                .build();
        return Collections.singletonList(choice);
    }

    public List<BotApiMethod<?>> caseDeleteSlot(long chatId, String username) {
        log.info("Command /slot/delete");
        Optional<StaffModel> staffModel = getStaffByUsername.getStaffByUsername(username);

        if (staffModel.isEmpty()) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
        }

        List<SlotModel> slotModel = getAllSlotsByStaff.getAllSlotsByStaff(
                staffModel.get(),
                null,
                true,
                Pagination.START_INDEX_PAGE.getTemplate(),
                Pagination.PAGE_SIZE.getTemplate()
        );

        if (slotModel.isEmpty()) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.NULL_SLOTS.getTemplate(), chatId);
        }

        userStateCache.setCache(chatId, new UserStateCacheModel(username, System.currentTimeMillis()));
        SendMessage choice = SendMessage.builder()
                .chatId(chatId)
                .text(MessageAndCallbackEnum.CHOICE_CANCEL.getTemplate())
                .replyMarkup(keyboardFactory.buildSlotKeyboard(slotModel, "delete", Pagination.START_INDEX_PAGE.getTemplate(), true, true))
                .build();
        return Collections.singletonList(choice);
    }

    public List<BotApiMethod<?>> caseSetSlot(long chatId, String username, String text) {
        log.info("Command /slot/*/*");

        String[] url = text.split("/");
        Optional<StaffModel> staffModel = getStaffByUsername.getStaffByUsername(username);

        if (staffModel.isEmpty()) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
        }

        if (url.length != 4) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.VALIDATE_NEW_SLOT.getTemplate(), chatId);
        }

        try {
            LocalDate date = LocalDate.parse(url[2]);
            LocalTime time = LocalTime.parse(url[3]);
            if (date.isBefore(LocalDate.now())) {
                return builderMessage.buildMessage(MessageAndCallbackEnum.DATE_NOT_PAST.getTemplate(), chatId);
            }

            setSlot.setSlots(new SlotModel(staffModel.get(), date, time));
            staffModel.get().setLastActivityAddedSlotDate(LocalDate.now());
            updateStaff.updateStaff(staffModel.get());

        } catch (Exception e) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.VALIDATE_NEW_SLOT.getTemplate(), chatId);
        }
        return builderMessage.buildMessage(MessageAndCallbackEnum.ADD_NEW_SLOT.getTemplate(), chatId);
    }

    public List<BotApiMethod<?>> caseMasterInfo(long chatId, String username){
        log.info("Command /master");

        List<BotApiMethod<?>> actions = new ArrayList<>();
        Optional<StaffModel> staffModel = getStaffByUsername.getStaffByUsername(username);

        if (staffModel.isEmpty()) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
        } else if (staffModel.get().getChatId() == 0) {
            staffModel.get().setChatId(chatId);
            updateStaff.updateStaff(staffModel.get());
            actions.add(SendMessage.builder()
                    .chatId(chatId)
                    .text(MasterEnum.HI_MASTER.getTemplate())
                    .parseMode("HTML")
                    .build());
        }

        actions.add(SendMessage.builder()
                .chatId(chatId)
                .text(MasterEnum.MASTER_INFO.getTemplate())
                .parseMode("HTML")
                .build());

        return actions;
    }

    public List<BotApiMethod<?>> caseSetSlotV2(long chatId, String username) {
        log.info("Command /slot/add");

        Optional<StaffModel> staffModel = getStaffByUsername.getStaffByUsername(username);

        if (staffModel.isEmpty()) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
        }

        SendMessage month = SendMessage.builder()
                .chatId(chatId)
                .text(MessageAndCallbackEnum.CHOOSE_MONTH.getTemplate())
                .replyMarkup(keyboardFactory
                        .createGridKeyboard(
                                MonthEnum.getMonthNominativeNames(),
                                "setSlotMonth",
                                3,
                                MonthEnum.getByNumber(YearMonth.now().getMonthValue()).getMonthNominative()
                        ))
                .build();
        return Collections.singletonList(month);
    }
}
