package example.ru.freeslotbottg.cache.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserStateCacheModel {
    private String auxiliaryField;
    private long lastAction;
}
