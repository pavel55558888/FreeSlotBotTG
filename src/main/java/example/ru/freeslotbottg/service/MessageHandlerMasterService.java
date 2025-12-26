package example.ru.freeslotbottg.service;

import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.service.slots.GetAllSlotsByStaff;
import example.ru.freeslotbottg.database.service.slots.SetSlot;
import example.ru.freeslotbottg.database.service.staff.*;
import example.ru.freeslotbottg.enums.MasterEnum;
import example.ru.freeslotbottg.enums.MessageHandlerEnum;
import example.ru.freeslotbottg.util.KeyboardFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private final BuilderMessageService builderMessageService;

    public List<BotApiMethod<?>> caseCheckAllSlots(long chatId, String username) {
        log.info("Command /slot/all");
        Optional<StaffModel> staffModel = getStaffByUsername.getStaffByUsername(username);

        if (staffModel.isEmpty()) {
            return builderMessageService.buildMessage(MessageHandlerEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
        }

        List<SlotModel> slotModel = getAllSlotsByStaff.getAllSlotsByStaff(staffModel.get());

        if (slotModel.isEmpty()) {
            return builderMessageService.buildMessage(MessageHandlerEnum.NULL_SLOTS.getTemplate(), chatId);
        }

        SendMessage choice = SendMessage.builder()
                .chatId(chatId)
                .text(MessageHandlerEnum.YOUR_SLOT.getTemplate())
                .replyMarkup(keyboardFactory.buildSlotKeyboard(slotModel, "check_slot", true, true))
                .build();
        return Collections.singletonList(choice);
    }

    public List<BotApiMethod<?>> caseDeleteSlot(long chatId, String username) {
        log.info("Command /slot/delete");
        Optional<StaffModel> staffModel = getStaffByUsername.getStaffByUsername(username);

        if (staffModel.isEmpty()) {
            return builderMessageService.buildMessage(MessageHandlerEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
        }

        List<SlotModel> slotModel = getAllSlotsByStaff.getAllSlotsByStaff(staffModel.get());

        if (slotModel.isEmpty()) {
            return builderMessageService.buildMessage(MessageHandlerEnum.NULL_SLOTS.getTemplate(), chatId);
        }

        SendMessage choice = SendMessage.builder()
                .chatId(chatId)
                .text(MessageHandlerEnum.CHOICE_CANCEL.getTemplate())
                .replyMarkup(keyboardFactory.buildSlotKeyboard(slotModel, "delete", true, false))
                .build();
        return Collections.singletonList(choice);
    }

    public List<BotApiMethod<?>> caseSetSlot(long chatId, String username, String text) {
        log.info("Command /slot/*/*");

        String[] url = text.split("/");
        Optional<StaffModel> staffModel = getStaffByUsername.getStaffByUsername(username);

        if (staffModel.isEmpty()) {
            return builderMessageService.buildMessage(MessageHandlerEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
        }

        if (url.length != 4) {
            return builderMessageService.buildMessage(MessageHandlerEnum.VALIDATE_NEW_SLOT.getTemplate(), chatId);
        }

        try {
            LocalDate date = LocalDate.parse(url[2]);
            LocalTime time = LocalTime.parse(url[3]);
            if (date.isBefore(LocalDate.now())) {
                return builderMessageService.buildMessage(MessageHandlerEnum.DATE_NOT_PAST.getTemplate(), chatId);
            }

            setSlot.setSlots(new SlotModel(staffModel.get(), date, time));

        } catch (Exception e) {
            return builderMessageService.buildMessage(MessageHandlerEnum.VALIDATE_NEW_SLOT.getTemplate(), chatId);
        }
        return builderMessageService.buildMessage(MessageHandlerEnum.ADD_NEW_SLOT.getTemplate(), chatId);
    }

    public List<BotApiMethod<?>> caseMasterInfo(long chatId, String username){
        log.info("Command /master");

        List<BotApiMethod<?>> actions = new ArrayList<>();
        Optional<StaffModel> staffModel = getStaffByUsername.getStaffByUsername(username);

        if (staffModel.isEmpty()) {
            return builderMessageService.buildMessage(MessageHandlerEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
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
}
