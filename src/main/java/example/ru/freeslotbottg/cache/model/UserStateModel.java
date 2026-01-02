package example.ru.freeslotbottg.cache.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserStateModel {
    private String auxiliaryField;
    private long lastAction;
}
