package example.ru.freeslotbottg.bot;

import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.service.profesion.GetByProfession;
import example.ru.freeslotbottg.database.service.slots.DeleteSlotById;
import example.ru.freeslotbottg.database.service.slots.UpdateSlot;
import example.ru.freeslotbottg.database.service.slots.GetAllSlotsByStaff;
import example.ru.freeslotbottg.database.service.slots.GetSlotById;
import example.ru.freeslotbottg.database.service.staff.GetStaffByFirstNameAndLastName;
import example.ru.freeslotbottg.database.service.staff.GetStaffByProfessionId;
import example.ru.freeslotbottg.util.KeyboardFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class CallbackHandler {

    private final KeyboardFactory keyboardFactory;
    private final GetByProfession getByProfession;
    private final GetStaffByProfessionId getStaffByProfessionId;
    private final GetStaffByFirstNameAndLastName getStaffByFirstNameAndLastName;
    private final GetAllSlotsByStaff getAllSlotsByStaff;
    private final GetSlotById getSlotById;
    private final UpdateSlot updateSlot;
    private final DeleteSlotById deleteSlotById;

    public List<BotApiMethod<?>> handle(CallbackQuery query) {
        String data = query.getData();
        long chatId = query.getMessage().getChatId();
        int messageId = query.getMessage().getMessageId();

        List<BotApiMethod<?>> actions = new ArrayList<>();
        actions.add(AnswerCallbackQuery.builder()
                .callbackQueryId(query.getId())
                .build());

        if (data == null) {
            return actions;
        }

        String[] parts = data.split(":", 2);
        if (parts.length != 2) {
            return actions;
        }

        String action = parts[0];
        String value = parts[1];

        if ("activity".equals(action)) {
            actions.add(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text("Вы выбрали: <b>" + value + "</b>")
                    .parseMode("HTML")
                    .build());

            List<String> masters = getStaffByProfessionId
                    .getStaffByProfessionId(
                            getByProfession
                                    .getByProfession(value)
                                    .getId()
                    )
                    .stream()
                    .map(StaffModel::toString)
                    .toList();

            if (masters.isEmpty()) {
                actions.add(SendMessage.builder()
                        .chatId(chatId)
                        .text("Нет доступных мастеров в этой категории.")
                        .build());
            } else {
                InlineKeyboardMarkup keyboard = keyboardFactory.createKeyboard(masters, "master");
                actions.add(SendMessage.builder()
                        .chatId(chatId)
                        .text("Выберите мастера:")
                        .replyMarkup(keyboard)
                        .build());

            }

        } else if ("master".equals(action)) {
            actions.add(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text("Вы выбрали: <b>" + value + "</b>")
                    .parseMode("HTML")
                    .build());

            List<SlotModel> slots = getAllSlotsByStaff.
                    getAllSlotsByStaff(
                            getStaffByFirstNameAndLastName
                                    .getStaffByFirstNameAndLastName(value)
                    )
                    .stream()
                    .filter(SlotModel::isAvailable)
                    .toList();

            if (slots.isEmpty()) {
                actions.add(SendMessage.builder()
                        .chatId(chatId)
                        .text("Нет свободных слотов у этого мастера.")
                        .build());
            } else {
                InlineKeyboardMarkup keyboard = keyboardFactory.buildSlotKeyboard(slots, "slot", true, false);
                actions.add(SendMessage.builder()
                        .chatId(chatId)
                        .text("Выберите слот:")
                        .replyMarkup(keyboard)
                        .build());
            }

        } else if ("slot".equals(action)) {
            SlotModel slot = getSlotById.getSlotById(Long.parseLong(value));
            if (slot != null && slot.isAvailable()) {
                slot.setAvailable(false);
                String username = query.getFrom().getUserName();
                slot.setUsernameClient(username);
                slot.setChatId(chatId);
                updateSlot.updateSlot(slot);

                actions.add(EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .text("✅ Вы успешно записаны!\n" +
                                "Мастер: <b>" + slot.getStaffModel() + "</b>\n" +
                                "Дата и время: <b>" + slot.getDate() + " " + slot.getTime() + "</b>\n" +
                                "За день до записи отправим вам уведомление")
                        .parseMode("HTML")
                        .build());

                actions.add(SendMessage.builder()
                        .chatId(slot.getStaffModel().getChatId())
                        .text(
                                "🔔 <b>К вам новая запись!</b>\n" +
                                "\n" +
                                "👤 <b>Клиент:</b> " + query.getFrom().getFirstName() + "\n" +
                                "🔖 <b>Telegram:</b> @" + query.getFrom().getUserName() + "\n" +
                                "📅 <b>Дата:</b> " + slot.getDate() + "\n" +
                                "⏰ <b>Время:</b> " + slot.getTime() + "\n"
                        )
                        .parseMode("HTML")
                        .build());
            } else {
                actions.add(EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .text("Кажется у вас из подноса украли запись \uD83E\uDD28 \n Попрбуйте выбрать другое время")
                        .build());
            }
        }else if ("canceled".equals(action)) {
            Optional<SlotModel> slot = Optional.ofNullable(getSlotById.getSlotById(Long.parseLong(value)));
            if (slot.isEmpty()){
                actions.add(EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .text("Ошибка сервера: запись не найдена")
                        .build());
                return actions;
            }
            String userNameClint = slot.get().getUsernameClient();
            slot.get().setAvailable(true);
            slot.get().setChatId(0);
            slot.get().setUsernameClient(null);

            updateSlot.updateSlot(slot.get());

            actions.add(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text("Ваша запись успешно отменена\n" +
                            "👤 Мастер: <b>" + slot.get().getStaffModel() + "</b>\n" +
                            "📅 Дата: <b>" + slot.get().getDate() + "</b>\n" +
                            "⏰ Время: <b>" + slot.get().getTime() + "</b>\n")
                    .parseMode("HTML")
                    .build());

            actions.add(SendMessage.builder()
                    .chatId(slot.get().getStaffModel().getChatId())
                    .text("Клиент отменил запись:\n" +
                            "📅 Дата: <b>" + slot.get().getDate() + "</b>\n" +
                            "⏰ Время: <b>" + slot.get().getTime() + "</b>\n" +
                            "🔖 Telegram: @<b>" + userNameClint + "</b>\n")
                    .parseMode("HTML")
                    .build());
        } else if ("delete".equals(action)) {
            Optional<SlotModel> slot = Optional.ofNullable(getSlotById.getSlotById(Long.parseLong(value)));
            if (slot.isEmpty()){
                actions.add(EditMessageText.builder()
                        .chatId(chatId)
                        .messageId(messageId)
                        .text("Ошибка сервера: запись не найдена")
                        .build());
                return actions;
            }

            if (!slot.get().isAvailable()) {
                actions.add(SendMessage.builder()
                        .chatId(slot.get().getChatId())
                        .text("Слот на который вы были записаны удалили, выберите новый слот, пожалуйста.\n\n" +
                                "👤 Мастер: <b>" + slot.get().getStaffModel() + "</b>\n" +
                                "📅 Дата: <b>" + slot.get().getDate() + "</b>\n" +
                                "⏰ Время: <b>" + slot.get().getTime() + "</b>\n")
                                .parseMode("HTML")
                        .build());
            }

            deleteSlotById.deleteSlot(Long.parseLong(value));
            actions.add(EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text("Слот успешно удален")
                    .build());

        } else if ("check_slot".equals(action)) {
            Optional<SlotModel> slot = Optional.ofNullable(getSlotById.getSlotById(Long.parseLong(value)));

            if (slot.isEmpty()) {
                actions.add(SendMessage.builder()
                        .chatId(chatId)
                        .text("Ошибка: такого слота не сууществует")
                        .parseMode("HTML")
                        .build());
                return actions;
            }

            if (!slot.get().isAvailable()) {
                actions.add(SendMessage.builder()
                        .chatId(chatId)
                        .text("Подробная информация о слоте:\n" +
                                "📅 Дата: <b>" + slot.get().getDate() + "</b>\n" +
                                "⏰ Время: <b>" + slot.get().getTime() + "</b>\n" +
                                "🔖 Telegram: @<b>" + slot.get().getUsernameClient() + "</b>\n")
                        .parseMode("HTML")
                        .build());
            } else {
                actions.add(SendMessage.builder()
                        .chatId(chatId)
                        .text("Свободный слот: \n" +
                                "📅 Дата: <b>" + slot.get().getDate() + "</b>\n" +
                                "⏰ Время: <b>" + slot.get().getTime() + "</b>\n")
                        .parseMode("HTML")
                        .build());
            }
        }

        return actions;
    }
}