package example.ru.freeslotbottg.service;

import example.ru.freeslotbottg.cache.UserStateCache;
import example.ru.freeslotbottg.cache.model.UserStateCacheModel;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.service.profesion.GetAllProfession;
import example.ru.freeslotbottg.database.service.slots.GetSlotsByUsernameClient;
import example.ru.freeslotbottg.enums.MessageAndCallbackEnum;
import example.ru.freeslotbottg.enums.Pagination;
import example.ru.freeslotbottg.enums.StartEnums;
import example.ru.freeslotbottg.util.BuilderMessage;
import example.ru.freeslotbottg.util.KeyboardFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageHandlerUserService {
    private final KeyboardFactory keyboardFactory;
    private final GetAllProfession getAllProfession;
    private final GetSlotsByUsernameClient getSlotsByUsernameClient;
    private final BuilderMessage builderMessage;
    private final UserStateCache userStateCache;

    public List<BotApiMethod<?>> caseStart(long chatId, String firstName){
        log.info("Command /start");
        SendMessage welcome = SendMessage.builder()
                .chatId(chatId)
                .text(StartEnums.START_MESSAGE.format(firstName))
                .build();

        SendMessage choice = SendMessage.builder()
                .chatId(chatId)
                .text(MessageAndCallbackEnum.CHOICE_PROFESSION.getTemplate())
                .replyMarkup(keyboardFactory.createKeyboard(
                        getAllProfession.getAllProfessions(
                                true,
                                Pagination.START_INDEX_PAGE.getTemplate(),
                                Pagination.PAGE_SIZE.getTemplate()
                        ), "activity", Pagination.START_INDEX_PAGE.getTemplate()))
                .build();

        return Arrays.asList(welcome, choice);
    }

    public List<BotApiMethod<?>> caseMySlots(long chatId, String username){
        log.info("Command /my_slots");
        List<SlotModel> slots = getSlotsByUsernameClient.getSlotsByUsername(
                username,
                false,
                0,
                0
        );

        if (slots.isEmpty()) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.NULL_SLOTS.getTemplate(), chatId);
        }

        List<BotApiMethod<?>> actions = new ArrayList<>();
        for (SlotModel slot : slots) {
            String text = MessageAndCallbackEnum.SLOT_DETAILS.format(Map.of(
                    "profession", slot.getStaffModel().getProfession().getProfession_type(),
                    "date", slot.getDate().toString(),
                    "time", slot.getTime().toString(),
                    "masterFullName", slot.getStaffModel().getFirstName() + " " + slot.getStaffModel().getLastName()
            ));

            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .parseMode("HTML")
                    .build();
            actions.add(message);
        }
        return actions;
    }

    public List<BotApiMethod<?>> caseCanceledSlot(long chatId, String username){
        log.info("Command /cancel_slot");
        List<SlotModel> slots = getSlotsByUsernameClient.getSlotsByUsername(
                username,
                true,
                Pagination.START_INDEX_PAGE.getTemplate(),
                Pagination.PAGE_SIZE.getTemplate()
        );

        if (slots.isEmpty()) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.NULL_SLOTS.getTemplate(), chatId);
        }

        userStateCache.setCache(chatId, new UserStateCacheModel(username, System.currentTimeMillis()));
        SendMessage choice = SendMessage.builder()
                .chatId(chatId)
                .text(MessageAndCallbackEnum.CHOICE_CANCEL.getTemplate())
                .replyMarkup(keyboardFactory.buildSlotKeyboard(slots, "canceled", Pagination.START_INDEX_PAGE.getTemplate(), false, false))
                .build();

        return Collections.singletonList(choice);
    }

}
