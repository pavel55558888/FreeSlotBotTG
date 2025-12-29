package example.ru.freeslotbottg.service;

import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.service.profesion.GetByProfession;
import example.ru.freeslotbottg.database.service.slots.GetAllSlotsByStaff;
import example.ru.freeslotbottg.database.service.slots.GetSlotById;
import example.ru.freeslotbottg.database.service.slots.UpdateSlot;
import example.ru.freeslotbottg.database.service.staff.GetStaffByFirstNameAndLastName;
import example.ru.freeslotbottg.database.service.staff.GetStaffByProfessionId;
import example.ru.freeslotbottg.enums.CallbackHandlerEnum;
import example.ru.freeslotbottg.util.KeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CallbackHandlerUserService {
    private final KeyboardFactory keyboardFactory;
    private final GetByProfession getByProfession;
    private final GetStaffByProfessionId getStaffByProfessionId;
    private final GetStaffByFirstNameAndLastName getStaffByFirstNameAndLastName;
    private final GetAllSlotsByStaff getAllSlotsByStaff;
    private final GetSlotById getSlotById;
    private final UpdateSlot updateSlot;

    public List<BotApiMethod<?>> caseActivity(List<BotApiMethod<?>> actions, long chatId, int messageId, String value) {
        actions.add(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(CallbackHandlerEnum.YOU_SELECTED.format(Map.of("value", value)))
                .parseMode("HTML")
                .build());

        List<String> masters = getStaffByProfessionId
                .getStaffByProfessionId(
                        getByProfession.getByProfession(value).getId()
                )
                .stream()
                .map(StaffModel::toString)
                .toList();

        if (masters.isEmpty()) {
            actions.add(SendMessage.builder()
                    .chatId(chatId)
                    .text(CallbackHandlerEnum.NO_MASTERS_IN_CATEGORY.getTemplate())
                    .build());
        } else {
            InlineKeyboardMarkup keyboard = keyboardFactory.createKeyboard(masters, "master");
            actions.add(SendMessage.builder()
                    .chatId(chatId)
                    .text(CallbackHandlerEnum.CHOOSE_MASTER.getTemplate())
                    .replyMarkup(keyboard)
                    .build());
        }
        return actions;
    }

    public List<BotApiMethod<?>> caseMaster(List<BotApiMethod<?>> actions, long chatId, int messageId, String value) {
        actions.add(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(CallbackHandlerEnum.YOU_SELECTED.format(Map.of("value", value)))
                .parseMode("HTML")
                .build());

        List<SlotModel> slots = getAllSlotsByStaff
                .getAllSlotsByStaff(getStaffByFirstNameAndLastName.getStaffByFirstNameAndLastName(value))
                .stream()
                .filter(SlotModel::isAvailable)
                .toList();

        if (slots.isEmpty()) {
            actions.add(SendMessage.builder()
                    .chatId(chatId)
                    .text(CallbackHandlerEnum.NO_SLOTS_FOR_MASTER.getTemplate())
                    .build());
        } else {
            InlineKeyboardMarkup keyboard = keyboardFactory.buildSlotKeyboard(slots, "slot", true, false);
            actions.add(SendMessage.builder()
                    .chatId(chatId)
                    .text(CallbackHandlerEnum.CHOOSE_SLOT.getTemplate())
                    .replyMarkup(keyboard)
                    .build());
        }
        return actions;
    }

    public List<BotApiMethod<?>> caseSlot(List<BotApiMethod<?>> actions, long chatId, int messageId,
                                          String value, String username, String firstName, String lastName) {
        SlotModel slot = getSlotById.getSlotById(Long.parseLong(value));

        if (slot != null && slot.isAvailable()) {
            slot.setAvailable(false);
            slot.setUsernameClient(username);
            slot.setChatId(chatId);
            updateSlot.updateSlot(slot);

            actions.add(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(CallbackHandlerEnum.SUCCESS_BOOKING.format(Map.of(
                            "master", slot.getStaffModel().toString(),
                            "date", slot.getDate().toString(),
                            "time", slot.getTime().toString()
                    )))
                    .parseMode("HTML")
                    .build());

            actions.add(SendMessage.builder()
                    .chatId(slot.getStaffModel().getChatId())
                    .text(CallbackHandlerEnum.NOTIFICATION_TO_MASTER_NEW_BOOKING.format(Map.of(
                            "clientName", firstName + " " + lastName,
                            "username", username != null ? username : "—",
                            "date", slot.getDate().toString(),
                            "time", slot.getTime().toString()
                    )))
                    .parseMode("HTML")
                    .build());
        } else {
            actions.add(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(CallbackHandlerEnum.SLOT_ALREADY_TAKEN.getTemplate())
                    .build());
        }
        return actions;
    }

    public List<BotApiMethod<?>> caseCanceled(List<BotApiMethod<?>> actions, long chatId, int messageId, String value, String firstName, String lastName) {
        Optional<SlotModel> slotOpt = Optional.ofNullable(getSlotById.getSlotById(Long.parseLong(value)));

        if (slotOpt.isEmpty()) {
            actions.add(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(CallbackHandlerEnum.ERROR_FORBIDDEN.getTemplate())
                    .build());
            return actions;
        }

        SlotModel slot = slotOpt.get();
        String userNameClient = slot.getUsernameClient();

        slot.setAvailable(true);
        slot.setChatId(0L);
        slot.setUsernameClient(null);
        updateSlot.updateSlot(slot);

        actions.add(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(CallbackHandlerEnum.CANCELLATION_SUCCESS.format(Map.of(
                        "master", slot.getStaffModel().toString(),
                        "date", slot.getDate().toString(),
                        "time", slot.getTime().toString()
                )))
                .parseMode("HTML")
                .build());

        actions.add(SendMessage.builder()
                .chatId(slot.getStaffModel().getChatId())
                .text(CallbackHandlerEnum.NOTIFICATION_TO_MASTER_CANCELLATION.format(Map.of(
                        "date", slot.getDate().toString(),
                        "time", slot.getTime().toString(),
                        "clientName", firstName + " " + lastName,
                        "usernameClient", userNameClient != null ? userNameClient : "—"
                )))
                .parseMode("HTML")
                .build());

        return actions;
    }
}
