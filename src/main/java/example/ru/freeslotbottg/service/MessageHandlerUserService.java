package example.ru.freeslotbottg.service;

import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.repo.ProfessionRepo;
import example.ru.freeslotbottg.database.service.slots.GetSlotsByUsernameClient;
import example.ru.freeslotbottg.enums.MessageHandlerEnum;
import example.ru.freeslotbottg.enums.StartEnums;
import example.ru.freeslotbottg.util.KeyboardFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageHandlerUserService {
    private final KeyboardFactory keyboardFactory;
    private final ProfessionRepo professionRepo;
    private final GetSlotsByUsernameClient getSlotsByUsernameClient;
    private final BuilderMessageService builderMessageService;

    public List<BotApiMethod<?>> caseStart(long chatId, String firstName){
        log.info("Command /start");
        SendMessage welcome = SendMessage.builder()
                .chatId(chatId)
                .text(StartEnums.START_MESSAGE.format(firstName))
                .build();

        SendMessage choice = SendMessage.builder()
                .chatId(chatId)
                .text(MessageHandlerEnum.CHOICE_PROFESSION.getTemplate())
                .replyMarkup(keyboardFactory.createKeyboard(professionRepo.getAllProfessions(), "activity"))
                .build();

        return Arrays.asList(welcome, choice);
    }

    public List<BotApiMethod<?>> caseMySlots(long chatId, String username){
        log.info("Command /my_slots");
        List<SlotModel> slots = getSlotsByUsernameClient.getSlotsByUsername(username);

        if (slots.isEmpty()) {
            return builderMessageService.buildMessage(MessageHandlerEnum.NULL_SLOTS.getTemplate(), chatId);
        }

        List<BotApiMethod<?>> actions = new ArrayList<>();
        for (SlotModel slot : slots) {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(slot.toString())
                    .build();
            actions.add(message);
        }
        return actions;
    }

    public List<BotApiMethod<?>> caseCanceledSlot(long chatId, String username){
        log.info("Command /cancel_slot");
        List<SlotModel> slots = getSlotsByUsernameClient.getSlotsByUsername(username);

        if (slots.isEmpty()) {
            return builderMessageService.buildMessage(MessageHandlerEnum.NULL_SLOTS.getTemplate(), chatId);
        }

        SendMessage choice = SendMessage.builder()
                .chatId(chatId)
                .text(MessageHandlerEnum.CHOICE_CANCEL.getTemplate())
                .replyMarkup(keyboardFactory.buildSlotKeyboard(slots, "canceled", false, false))
                .build();

        return Collections.singletonList(choice);
    }

}
