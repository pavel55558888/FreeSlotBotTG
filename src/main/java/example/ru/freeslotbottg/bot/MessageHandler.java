package example.ru.freeslotbottg.bot;

import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.repo.ProfessionRepo;
import example.ru.freeslotbottg.database.service.slots.GetSlotsByUsername;
import example.ru.freeslotbottg.enums.StartEnums;
import example.ru.freeslotbottg.util.KeyboardFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class MessageHandler {

    private final KeyboardFactory keyboardFactory;
    private final ProfessionRepo professionRepo;
    private final GetSlotsByUsername getSlotsByUsername;

    public List<BotApiMethod<?>> handle(Update update) {
        long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        String firstName = update.getMessage().getFrom().getFirstName();
        String username = update.getMessage().getFrom().getUserName();

        if ("/start".equals(text)) {
            SendMessage welcome = SendMessage.builder()
                    .chatId(chatId)
                    .text(StartEnums.START_MESSAGE.format(firstName))
                    .build();

            SendMessage choice = SendMessage.builder()
                    .chatId(chatId)
                    .text("Выберите вид деятельности:")
                    .replyMarkup(keyboardFactory.createKeyboard(professionRepo.getAllProfessions(), "activity"))
                    .build();

            return Arrays.asList(welcome, choice);
        } else if ("/my_slots".equals(text)) {
            List<SlotModel> slots = getSlotsByUsername.getSlotsByUsername(username);

            if (slots.isEmpty()) {
                return slotsEmpty(chatId);
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
        } else if("/canceled_slot".equals(text)) {
            List<SlotModel> slots = getSlotsByUsername.getSlotsByUsername(username);

            if (slots.isEmpty()) {
                return slotsEmpty(chatId);
            }

            SendMessage choice = SendMessage.builder()
                    .chatId(chatId)
                    .text("Выберите запись для отмены:")
                    .replyMarkup(keyboardFactory.buildSlotKeyboard(slots, "canceled", false))
                    .build();

            return Collections.singletonList(choice);
        } else {
            return List.of(SendMessage.builder()
                    .chatId(chatId)
                    .text("Данной команды не существует")
                    .build());
        }
    }

    private List<BotApiMethod<?>> slotsEmpty(long chatId){
        SendMessage noSlots = SendMessage.builder()
                .chatId(chatId)
                .text("У вас нет записей.")
                .build();
        return Collections.singletonList(noSlots);
    }
}