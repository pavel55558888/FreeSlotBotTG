package example.ru.freeslotbottg.service.pagination;

import example.ru.freeslotbottg.cache.UserStateCache;
import example.ru.freeslotbottg.cache.model.UserStateModel;
import example.ru.freeslotbottg.database.model.SlotModel;
import example.ru.freeslotbottg.database.model.StaffModel;
import example.ru.freeslotbottg.database.service.slots.GetAllSlotsByStaff;
import example.ru.freeslotbottg.database.service.staff.GetStaffByUsername;
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
public class CallbackHandlerMasterPaginationService {
    private final PaginationUtil paginationUtil;
    private final UserStateCache userStateCache;
    private final SessionTimeHasExpired sessionTimeHasExpired;
    private final GetStaffByUsername getStaffByUsername;
    private final GetAllSlotsByStaff getAllSlotsByStaff;

    public List<BotApiMethod<?>> caseDeletePagination(List<BotApiMethod<?>> actions, long chatId, int messageId, String value,
                                                      int page, String prefix){
        int pageAfterIndexing = paginationUtil.indexingPage(value, page);

        if (pageAfterIndexing < 0){
            return actions;
        }

        Optional<UserStateModel> userStateModel = userStateCache.getCache(chatId);
        if (userStateModel.isEmpty()){
            return sessionTimeHasExpired.sessionExpired(chatId);
        }
        Optional<StaffModel> staffModel = getStaffByUsername.getStaffByUsername(userStateModel.get().getAuxiliaryField());

        List<SlotModel> slotModel = getAllSlotsByStaff.getAllSlotsByStaff(
                staffModel.get(),
                null,
                true,
                pageAfterIndexing,
                Pagination.PAGE_SIZE.getTemplate()
        );

        return paginationUtil
                .paginationKeyboard(actions, pageAfterIndexing, chatId, messageId, prefix, slotModel,
                        true, true, true, MessageAndCallbackEnum.CHOICE_CANCEL.getTemplate());
    }

    public List<BotApiMethod<?>> caseCheckSlotsPagination(List<BotApiMethod<?>> actions, long chatId, int messageId, String value,
                                                      int page, String prefix) {
        int pageAfterIndexing = paginationUtil.indexingPage(value, page);

        if (pageAfterIndexing < 0) {
            return actions;
        }

        Optional<UserStateModel> userStateModel = userStateCache.getCache(chatId);
        if (userStateModel.isEmpty()) {
            return sessionTimeHasExpired.sessionExpired(chatId);
        }
        Optional<StaffModel> staffModel = getStaffByUsername.getStaffByUsername(userStateModel.get().getAuxiliaryField());

        List<SlotModel> slotModel = getAllSlotsByStaff.getAllSlotsByStaff(
                staffModel.get(),
                null,
                true,
                pageAfterIndexing,
                Pagination.PAGE_SIZE.getTemplate()
        );

        return paginationUtil
                .paginationKeyboard(actions, pageAfterIndexing, chatId, messageId, prefix, slotModel,
                        true, true, true, MessageAndCallbackEnum.YOUR_SLOT.getTemplate());
    }
}
