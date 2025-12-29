package example.ru.freeslotbottg.service;

import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.service.slots.DeleteSlotById;
import example.ru.freeslotbottg.database.service.slots.GetSlotById;
import example.ru.freeslotbottg.enums.CallbackHandlerEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CallbackHandlerMasterService {
    private final GetSlotById getSlotById;
    private final DeleteSlotById deleteSlotById;

    public List<BotApiMethod<?>> caseDelete(List<BotApiMethod<?>> actions, long chatId, int messageId, String value) {

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

        if (!slot.isAvailable()) {
            String messageText = CallbackHandlerEnum.SLOT_DELETED_NOTIFY.format(Map.of(
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
                .text(CallbackHandlerEnum.SLOT_DELETED_SUCCESS.getTemplate())
                .build());

        return actions;
    }

    public List<BotApiMethod<?>> caseCheckSlot(List<BotApiMethod<?>> actions, long chatId, String value, String firstName, String lastName) {

        Optional<SlotModel> slotOpt = Optional.ofNullable(getSlotById.getSlotById(Long.parseLong(value)));

        if (slotOpt.isEmpty()) {
            actions.add(SendMessage.builder()
                    .chatId(chatId)
                    .text(CallbackHandlerEnum.SLOT_NOT_FOUND.getTemplate())
                    .parseMode("HTML")
                    .build());
            return actions;
        }

        SlotModel slot = slotOpt.get();

        if (!slot.isAvailable()) {
            actions.add(SendMessage.builder()
                    .chatId(chatId)
                    .text(CallbackHandlerEnum.SLOT_INFO_TAKEN.format(Map.of(
                            "date", slot.getDate().toString(),
                            "time", slot.getTime().toString(),
                            "clientName", firstName + " " + lastName,
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
                    .text(CallbackHandlerEnum.SLOT_INFO_AVAILABLE.format(freeSlotReplacements))
                    .parseMode("HTML")
                    .build());
        }

        return actions;
    }

}
