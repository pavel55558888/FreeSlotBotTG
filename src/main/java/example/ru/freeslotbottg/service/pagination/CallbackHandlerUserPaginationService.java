package example.ru.freeslotbottg.service.pagination;

import example.ru.freeslotbottg.cache.UserStateCache;
import example.ru.freeslotbottg.cache.model.UserStateModel;
import example.ru.freeslotbottg.database.model.ProfessionModel;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.service.profesion.GetAllProfession;
import example.ru.freeslotbottg.database.service.profesion.GetByProfession;
import example.ru.freeslotbottg.database.service.slots.GetAllSlotsByStaff;
import example.ru.freeslotbottg.database.service.slots.GetSlotsByUsernameClient;
import example.ru.freeslotbottg.database.service.staff.GetStaffByFirstNameAndLastName;
import example.ru.freeslotbottg.database.service.staff.GetStaffByProfessionId;
import example.ru.freeslotbottg.enums.MessageAndCallbackEnum;
import example.ru.freeslotbottg.enums.Pagination;
import example.ru.freeslotbottg.util.PaginationUtil;
import example.ru.freeslotbottg.util.SessionTimeHasExpired;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CallbackHandlerUserPaginationService {
    private final PaginationUtil paginationUtil;
    private final GetAllProfession getAllProfession;
    private final UserStateCache userStateCache;
    private final SessionTimeHasExpired sessionTimeHasExpired;
    private final GetStaffByProfessionId getStaffByProfessionId;
    private final GetByProfession getByProfession;
    private final GetAllSlotsByStaff getAllSlotsByStaff;
    private final GetStaffByFirstNameAndLastName getStaffByFirstNameAndLastName;
    private final GetSlotsByUsernameClient getSlotsByUsernameClient;

    public List<BotApiMethod<?>> caseActivityPagination(List<BotApiMethod<?>> actions, long chatId, int messageId, String value,
                                                      int page, String prefix) {
        int pageAfterIndexing = paginationUtil.indexingPage(value, page);

        if (pageAfterIndexing < 0) {
            return actions;
        }

        List<ProfessionModel> professionModelList
                = getAllProfession.getAllProfessions(true, pageAfterIndexing, Pagination.PAGE_SIZE.getTemplate());

        return paginationUtil
                .paginationKeyboard(actions, pageAfterIndexing, chatId, messageId, prefix, professionModelList, false,
                        false, false, MessageAndCallbackEnum.CHOICE_PROFESSION.getTemplate());
    }

    public List<BotApiMethod<?>> caseMasterPagination(List<BotApiMethod<?>> actions, long chatId, int messageId, String value,
                                                        int page, String prefix) {
        int pageAfterIndexing = paginationUtil.indexingPage(value, page);

        if (pageAfterIndexing < 0) {
            return actions;
        }

        Optional<UserStateModel> userStateModel = userStateCache.getCache(chatId);
        if (userStateModel.isEmpty()) {
            return sessionTimeHasExpired.sessionExpired(chatId);
        }

        List<StaffModel> staffModelList
                = getStaffByProfessionId.getStaffByProfessionId(
                getByProfession.getByProfession(userStateModel.get().getAuxiliaryField()).getId(),
                true,
                pageAfterIndexing,
                Pagination.PAGE_SIZE.getTemplate()
        );

        return paginationUtil
                .paginationKeyboard(actions, pageAfterIndexing, chatId, messageId, prefix, staffModelList,
                        false, false, false, MessageAndCallbackEnum.CHOOSE_MASTER.getTemplate());
    }

    public List<BotApiMethod<?>> caseSlotPagination(List<BotApiMethod<?>> actions, long chatId, int messageId, String value,
                                                      int page, String prefix) {
        int pageAfterIndexing = paginationUtil.indexingPage(value, page);

        if (pageAfterIndexing < 0) {
            return actions;
        }

        Optional<UserStateModel> userStateModel = userStateCache.getCache(chatId);
        if (userStateModel.isEmpty()) {
            return sessionTimeHasExpired.sessionExpired(chatId);
        }

        List<SlotModel> slots = getAllSlotsByStaff
                .getAllSlotsByStaff(
                        getStaffByFirstNameAndLastName.getStaffByFirstNameAndLastName(userStateModel.get().getAuxiliaryField()),
                        true,
                        true,
                        pageAfterIndexing,
                        Pagination.PAGE_SIZE.getTemplate()
                );

        return paginationUtil
                .paginationKeyboard(actions, pageAfterIndexing, chatId, messageId, prefix, slots,
                        true, true, false, MessageAndCallbackEnum.CHOOSE_SLOT.getTemplate());
    }

    public List<BotApiMethod<?>> caseCanceledPagination(List<BotApiMethod<?>> actions, long chatId, int messageId, String value,
                                                    int page, String prefix, String username) {
        int pageAfterIndexing = paginationUtil.indexingPage(value, page);

        if (pageAfterIndexing < 0) {
            return actions;
        }

        Optional<UserStateModel> userStateModel = userStateCache.getCache(chatId);
        if (userStateModel.isEmpty()) {
            return sessionTimeHasExpired.sessionExpired(chatId);
        }

        List<SlotModel> slots = getSlotsByUsernameClient.getSlotsByUsername(
                username,
                true,
                pageAfterIndexing,
                Pagination.PAGE_SIZE.getTemplate()
        );

        return paginationUtil
                .paginationKeyboard(actions, pageAfterIndexing, chatId, messageId, prefix, slots,
                        true, false, false, MessageAndCallbackEnum.CHOICE_CANCEL.getTemplate());
    }
}
