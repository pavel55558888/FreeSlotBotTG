package example.ru.freeslotbottg.service;

import example.ru.freeslotbottg.database.model.ProfessionModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.service.profesion.DeleteProfession;
import example.ru.freeslotbottg.database.service.profesion.GetByProfession;
import example.ru.freeslotbottg.database.service.profesion.SetProfession;
import example.ru.freeslotbottg.database.service.staff.*;
import example.ru.freeslotbottg.enums.AdminEnum;
import example.ru.freeslotbottg.enums.MessageAndCallbackEnum;
import example.ru.freeslotbottg.util.BuilderMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Optional;

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

    @Value("${bot.admin}")
    private String admin;

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

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }
}
