package example.ru.freeslotbottg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Pagination {
    PAGE_SIZE(5),
    START_INDEX_PAGE(0);
    private final int template;
}
