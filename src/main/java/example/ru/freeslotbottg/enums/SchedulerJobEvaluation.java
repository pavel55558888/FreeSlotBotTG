package example.ru.freeslotbottg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SchedulerJobEvaluation {
    USER_FEEDBACK("📝 Оцените работу мастера, для нас это очень важно! 🙏"),
    ONE("⭐   😡"),
    TWO("⭐⭐   😕"),
    THREE("⭐⭐⭐   🙂"),
    FOUR("⭐⭐⭐⭐   😊"),
    FIVE("⭐⭐⭐⭐⭐   🤩");

    private final String template;
}
