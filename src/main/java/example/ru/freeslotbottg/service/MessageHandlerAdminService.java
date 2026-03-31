package example.ru.freeslotbottg.service;

import example.ru.freeslotbottg.database.model.ProfessionModel;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.service.profesion.DeleteProfession;
import example.ru.freeslotbottg.database.service.profesion.GetByProfession;
import example.ru.freeslotbottg.database.service.profesion.SetProfession;
import example.ru.freeslotbottg.database.service.slots.GetAllSlots;
import example.ru.freeslotbottg.database.service.staff.*;
import example.ru.freeslotbottg.enums.AdminEnum;
import example.ru.freeslotbottg.enums.MessageAndCallbackEnum;
import example.ru.freeslotbottg.enums.Pagination;
import example.ru.freeslotbottg.util.BuilderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageHandlerAdminService {
    private final SetProfession setProfession;
    private final GetByProfession getByProfession;
    private final SetStaff setStaff;
    private final GetStaffByUsername getStaffByUsername;
    private final DeleteStaff deleteStaff;
    private final DeleteProfession deleteProfession;
    private final GetStaffByProfessionId getStaffByProfessionId;
    private final BuilderMessage builderMessage;
    private final GetAllSlots getAllSlots;

    @Value("${bot.admin}")
    private String admin;
    @Value("${batch.size.db.slots.admin.push.notify}")
    private int batchSize;

    public List<BotApiMethod<?>> caseNewMaster(String username, long chatId, String text){
        log.info("Command /admin/new/master/**");

        if (!username.equals(admin)) {
            builderMessage.buildMessage(MessageAndCallbackEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
        }

        try {
            String[] url = text.split("/");
            url[5] = url[5].replace("@", "");

            Optional<ProfessionModel> professionModel = Optional.ofNullable(getByProfession.getByProfession(capitalize(url[4])));
            if(professionModel.isEmpty()) {
                setProfession.setProfession(new ProfessionModel(capitalize(url[4])));
            }

            setStaff.setStaff(
                    new StaffModel(
                            getByProfession.getByProfession(capitalize(url[4])),
                            capitalize(url[6]),
                            capitalize(url[7]),
                            url[5]
                    ));

            return builderMessage.buildMessage(
                    (professionModel.isEmpty() ? MessageAndCallbackEnum.NEW_PROFESSION.getTemplate() : "")
                            + MessageAndCallbackEnum.NEW_MASTER.getTemplate(), chatId);
        }catch (Exception e) {
            log.error("Ошибка при добавление нового мастер:\n" + e.getMessage());
            return builderMessage.buildMessage(MessageAndCallbackEnum.NOT_FOUND_ADMIN_NEW_MASTER.getTemplate(), chatId);
        }
    }

    public List<BotApiMethod<?>> caseDeleteMaster(long chatId, String username, String text){
        log.info("Command /admin/delete/master/*");

        if (!username.equals(admin)) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
        }
        String usernameStaff = text.split("/")[4].replace("@", "");
        Optional<StaffModel> staffModel = getStaffByUsername.getStaffByUsername(usernameStaff);

        if(staffModel.isEmpty()) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.MASTER_NULL.getTemplate(), chatId);
        }

        deleteStaff.deleteStaff(staffModel.get().getId());

        List<StaffModel> staffListByProfession
                = getStaffByProfessionId.getStaffByProfessionId(staffModel.get().getProfession().getId(), false, 0,0);

        if (staffListByProfession.isEmpty()) {
            deleteProfession.deleteProfession(staffModel.get().getProfession().getId());
        }
        return builderMessage.buildMessage((staffListByProfession.isEmpty()
                ? MessageAndCallbackEnum.DELETE_PROFESSION.getTemplate() : "") + MessageAndCallbackEnum.DELETE_MASTER.getTemplate(), chatId);

    }

    public List<BotApiMethod<?>> caseAdminInfo(long chatId, String username){
        log.info("Command /admin");

        if (!username.equals(admin)) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
        }

        return List.of(SendMessage.builder()
                .chatId(chatId)
                .text(AdminEnum.ADMIN_INFO.getTemplate())
                .parseMode("HTML")
                .build());
    }

    public List<BotApiMethod<?>> caseSendNotify(long chatId, String username, String text) {
        log.info("Command /admin/send/notify/*");

        if (!username.equals(admin)) {
            return builderMessage.buildMessage(MessageAndCallbackEnum.COMMAND_NOT_FOUND.getTemplate(), chatId);
        }

        List<BotApiMethod<?>> apiMethodList = new ArrayList<>();
        String message = text.replaceFirst("/admin/send/notify/", "");
        message = message.replace("\\n", "\n");

        List<SlotModel> slots;
        int page = Pagination.START_INDEX_PAGE.getTemplate();
        Set<Long> notifiedChatIds = new HashSet<>();

        do {
            slots = getAllSlots.getSlots(true, page, batchSize);

            for (SlotModel slot : slots) {
                if (slot.getClient() != null && notifiedChatIds.add(slot.getClient().getChatId())) {
                    apiMethodList.add(SendMessage.builder()
                            .chatId(slot.getClient().getChatId())
                            .text(message)
                            .parseMode("HTML")
                            .build());
                }
            }

            page++;

        } while (!slots.isEmpty());

        return apiMethodList;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }
}
