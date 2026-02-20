package example.ru.freeslotbottg.cache.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class SlotCacheModel {
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Integer minute;
    private Long startAt;

    public boolean objectIsNull(){
        if(year == null || month == null || day == null || hour == null || minute == null || startAt == null){
            return true;
        }
        else return false;
    }
}
