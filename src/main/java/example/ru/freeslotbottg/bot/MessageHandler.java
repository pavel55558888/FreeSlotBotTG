package example.ru.freeslotbottg.bot;

import example.ru.freeslotbottg.database.model.ProfessionModel;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.repo.ProfessionRepo;
import example.ru.freeslotbottg.database.service.profesion.GetByProfession;
import example.ru.freeslotbottg.database.service.profesion.SetProfession;
import example.ru.freeslotbottg.database.service.slots.GetSlotsByUsername;
import example.ru.freeslotbottg.database.service.slots.SetSlot;
import example.ru.freeslotbottg.database.service.staff.GetStaffByUsername;
import example.ru.freeslotbottg.database.service.staff.SetStaff;
import example.ru.freeslotbottg.enums.StartEnums;
import example.ru.freeslotbottg.util.KeyboardFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class MessageHandler {
    private final KeyboardFactory keyboardFactory;
    private final ProfessionRepo professionRepo;
    private final GetSlotsByUsername getSlotsByUsername;
    private final SetProfession setProfession;
    private final GetByProfession getByProfession;
    private final SetStaff setStaff;
    private final SetSlot setSlot;
    private final GetStaffByUsername getStaffByUsername;

    @Value("${bot.admin}")
    private String admin;

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
                return buildMessage("У вас нет записей.", chatId);
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
                return buildMessage("У вас нет записей.", chatId);
            }

            SendMessage choice = SendMessage.builder()
                    .chatId(chatId)
                    .text("Выберите запись для отмены:")
                    .replyMarkup(keyboardFactory.buildSlotKeyboard(slots, "canceled", false))
                    .build();

            return Collections.singletonList(choice);
        } else if(text.startsWith("/admin/new/profession/")) {
            /***
             * /admin/new/profession/профессия
             */
            if (!username.equals(admin)) {
                buildMessage("Данной команды не существует", chatId);
            }

            try {
                setProfession.setProfession(new ProfessionModel(capitalize(text.split("/")[4])));
            }catch (Exception e){
                return buildMessage("Неизвестная ошибка: формат /admin/new/profession/профессия", chatId);
            }

            return buildMessage("Вид деятельности успешно добавлен", chatId);
        } else if(text.startsWith("/admin/new/master/")) {
            /***
             * /admin/new/master/профессия/username/firstname/lastname
             */
            if (!username.equals(admin)) {
                buildMessage("Данной команды не существует", chatId);
            }

            try {
                String[] url = text.split("/");
                setStaff.setStaff(
                        new StaffModel(
                                getByProfession.getByProfession(capitalize(url[4])),
                                capitalize(url[6]),
                                capitalize(url[7]),
                                url[5]
                        ));

                return buildMessage("Новый мастер успешно добавлен", chatId);
            }catch (Exception e) {
                return buildMessage("Неизвестная ошибка: формат /admin/new/master/профессия/username/firstname/lastname", chatId);
            }
        } else if(text.startsWith("/slot")) {
            /***
             * /slot/YYYY-MM-DD/HH:mm
             */
            String[] url = text.split("/");
            Optional<StaffModel> staffModel = getStaffByUsername.getStaffByUsername(username);

            if (staffModel.isEmpty()) {
                return buildMessage("Данной команды не существует", chatId);
            }

            if (url.length != 4) {
                return buildMessage("Используйте: /slot/YYYY-MM-DD/HH:mm", chatId);
            }

            try {
                LocalDate date = LocalDate.parse(url[2]);
                LocalTime time = LocalTime.parse(url[3]);
                if (date.isBefore(LocalDate.now())) {
                    return buildMessage("Дата не может быть в прошлом", chatId);
                }

                setSlot.setSlots(new SlotModel(staffModel.get(), date, time));

            } catch (Exception e) {
                return buildMessage("Неверный формат даты или времени", chatId);
            }
            return buildMessage("Новый слот успешно добавлен", chatId);
        }else {
            return buildMessage("Данной команды не существует", chatId);
        }
    }


    private List<BotApiMethod<?>> buildMessage(String message, long chatId){
        return List.of(SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .build());
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }
}