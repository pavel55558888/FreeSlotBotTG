package example.ru.freeslotbottg.service;

import example.ru.freeslotbottg.cache.UserStateCache;
import example.ru.freeslotbottg.cache.model.UserStateCacheModel;
import example.ru.freeslotbottg.database.model.ClientModel;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.service.client.GetClientByUsername;
import example.ru.freeslotbottg.database.service.client.SetClient;
import example.ru.freeslotbottg.database.service.client.UpdateClient;
import example.ru.freeslotbottg.database.service.profesion.GetByProfession;
import example.ru.freeslotbottg.database.service.slots.GetAllSlotsByStaff;
import example.ru.freeslotbottg.database.service.slots.GetSlotById;
import example.ru.freeslotbottg.database.service.slots.UpdateSlot;
import example.ru.freeslotbottg.database.service.staff.GetStaffByFirstNameAndLastName;
import example.ru.freeslotbottg.database.service.staff.GetStaffByProfessionId;
import example.ru.freeslotbottg.enums.MessageAndCallbackEnum;
import example.ru.freeslotbottg.enums.Pagination;
import example.ru.freeslotbottg.service.pagination.CallbackHandlerUserPaginationService;
import example.ru.freeslotbottg.util.KeyboardFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CallbackHandlerUserService {
    private final KeyboardFactory keyboardFactory;
    private final GetByProfession getByProfession;
    private final GetStaffByProfessionId getStaffByProfessionId;
    private final GetStaffByFirstNameAndLastName getStaffByFirstNameAndLastName;
    private final GetAllSlotsByStaff getAllSlotsByStaff;
    private final GetSlotById getSlotById;
    private final UpdateSlot updateSlot;
    private final UserStateCache userStateCache;
    private final CallbackHandlerUserPaginationService callbackHandlerUserPaginationService;
    private final GetClientByUsername getClientByUsername;
    private final SetClient setClient;
    private final UpdateClient updateClient;

    public List<BotApiMethod<?>> caseActivity(List<BotApiMethod<?>> actions, long chatId,
                                              int messageId, String value, int page, String prefix) {
        if (!(page < 0)){
            return callbackHandlerUserPaginationService.caseActivityPagination(actions, chatId, messageId, value, page, prefix);
        }

        actions.add(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(MessageAndCallbackEnum.YOU_SELECTED.format(Map.of("value", value)))
                .parseMode("HTML")
                .build());

        List<String> masters = getStaffByProfessionId
                .getStaffByProfessionId(
                        getByProfession.getByProfession(value).getId(),
                        true,
                        Pagination.START_INDEX_PAGE.getTemplate(),
                        Pagination.PAGE_SIZE.getTemplate()
                )
                .stream()
                .map(StaffModel::toString)
                .toList();

        if (masters.isEmpty()) {
            actions.add(SendMessage.builder()
                    .chatId(chatId)
                    .text(MessageAndCallbackEnum.NO_MASTERS_IN_CATEGORY.getTemplate())
                    .build());
        } else {
            userStateCache.setCache(chatId, new UserStateCacheModel(value, System.currentTimeMillis()));
            InlineKeyboardMarkup keyboard = keyboardFactory.createKeyboard(masters, "master", Pagination.START_INDEX_PAGE.getTemplate());
            actions.add(SendMessage.builder()
                    .chatId(chatId)
                    .text(MessageAndCallbackEnum.CHOOSE_MASTER.getTemplate())
                    .replyMarkup(keyboard)
                    .build());
        }
        return actions;
    }

    public List<BotApiMethod<?>> caseMaster(List<BotApiMethod<?>> actions, long chatId, int messageId,
                                            String value, int page, String prefix, String username) {
        if (!(page < 0)){
            return callbackHandlerUserPaginationService.caseMasterPagination(actions, chatId, messageId, value, page, prefix);
        }

        StaffModel staffModel = getStaffByFirstNameAndLastName.getStaffByFirstNameAndLastName(value);

        actions.add(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(MessageAndCallbackEnum.YOU_SELECTED.format(Map.of("value", value)))
                .parseMode("HTML")
                .build());

        List<SlotModel> slots = getAllSlotsByStaff
                .getAllSlotsByStaff(
                        staffModel,
                        true,
                        true,
                        Pagination.START_INDEX_PAGE.getTemplate(),
                        Pagination.PAGE_SIZE.getTemplate()
                );

        Optional<ClientModel> clientModel = getClientByUsername.getClientByUsername(username);

        if (clientModel.isEmpty()){
            setClient.setClient(new ClientModel(List.of(staffModel), username, chatId));
        }else {
            boolean isSelectedNewMaster =
                    clientModel.get().getStaffModelList().stream()
                            .anyMatch(x ->
                                    value.equals(
                                            x.getFirstName() + " " + x.getLastName()
                                    )
                            );
            if (isSelectedNewMaster){
                clientModel.get().getStaffModelList().add(staffModel);
                updateClient.updateClient(clientModel.get());
            }
        }

        if (slots.isEmpty()) {
            actions.add(SendMessage.builder()
                    .chatId(chatId)
                    .text(MessageAndCallbackEnum.NO_SLOTS_FOR_MASTER.getTemplate())
                    .build());
        } else {
            userStateCache.setCache(chatId, new UserStateCacheModel(value, System.currentTimeMillis()));

            InlineKeyboardMarkup keyboard
                    = keyboardFactory.buildSlotKeyboard(
                            slots,
                    "slot",
                    Pagination.START_INDEX_PAGE.getTemplate(),
                    true,
                    false);

            actions.add(SendMessage.builder()
                    .chatId(chatId)
                    .text(MessageAndCallbackEnum.CHOOSE_SLOT.getTemplate())
                    .replyMarkup(keyboard)
                    .build());
        }
        return actions;
    }

    public List<BotApiMethod<?>> caseSlot(List<BotApiMethod<?>> actions, long chatId, int messageId, String value,
                                          String username, String firstName, String lastName, int page, String prefix) {
        if (!(page < 0)){
            return callbackHandlerUserPaginationService.caseSlotPagination(actions, chatId, messageId, value, page, prefix);
        }

        Optional<SlotModel> slot = Optional.ofNullable(getSlotById.getSlotById(Long.parseLong(value)));

        if (slot.isPresent() && slot.get().isAvailable()) {
            slot.get().setAvailable(false);
            slot.get().setUsernameClient(username);
            slot.get().setChatId(chatId);
            slot.get().setFirstNameClient(firstName);
            slot.get().setLastNameClient(lastName);
            updateSlot.updateSlot(slot.get());

            userStateCache.setCache(chatId, new UserStateCacheModel(value, System.currentTimeMillis()));
            actions.add(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(MessageAndCallbackEnum.SUCCESS_BOOKING.format(Map.of(
                            "master", slot.get().getStaffModel().toString(),
                            "date", slot.get().getDate().toString(),
                            "time", slot.get().getTime().toString()
                    )))
                    .parseMode("HTML")
                    .build());

            actions.add(SendMessage.builder()
                    .chatId(slot.get().getStaffModel().getChatId())
                    .text(MessageAndCallbackEnum.NOTIFICATION_TO_MASTER_NEW_BOOKING.format(Map.of(
                            "clientName", firstName + " " + lastName,
                            "username", username != null ? username : "—",
                            "date", slot.get().getDate().toString(),
                            "time", slot.get().getTime().toString()
                    )))
                    .parseMode("HTML")
                    .build());
        } else {
            actions.add(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text(MessageAndCallbackEnum.SLOT_ALREADY_TAKEN.getTemplate())
                    .build());
        }
        return actions;
    }

    public List<BotApiMethod<?>> caseCanceled(List<BotApiMethod<?>> actions, long chatId, int messageId, String value,
                                              String firstName, String lastName, int page, String prefix, String username) {
        if (!(page < 0)){
            return callbackHandlerUserPaginationService.caseCanceledPagination(actions, chatId, messageId, value, page, prefix, username);
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
        String userNameClient = slot.getUsernameClient();

        slot.setAvailable(true);
        slot.setChatId(0L);
        slot.setUsernameClient(null);
        updateSlot.updateSlot(slot);

        actions.add(EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(MessageAndCallbackEnum.CANCELLATION_SUCCESS.format(Map.of(
                        "master", slot.getStaffModel().toString(),
                        "date", slot.getDate().toString(),
                        "time", slot.getTime().toString()
                )))
                .parseMode("HTML")
                .build());

        actions.add(SendMessage.builder()
                .chatId(slot.getStaffModel().getChatId())
                .text(MessageAndCallbackEnum.NOTIFICATION_TO_MASTER_CANCELLATION.format(Map.of(
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
