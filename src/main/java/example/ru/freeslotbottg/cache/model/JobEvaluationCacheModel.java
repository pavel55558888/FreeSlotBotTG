package example.ru.freeslotbottg.cache.model;

import example.ru.freeslotbottg.database.model.SlotModel;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class JobEvaluationCacheModel {
    private SlotModel slotModel;
    private Long startAt;
}
