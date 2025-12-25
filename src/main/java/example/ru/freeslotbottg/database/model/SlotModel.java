package example.ru.freeslotbottg.database.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "slots")
@Getter
@Setter
@NoArgsConstructor
public class SlotModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private StaffModel staffModel;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private LocalTime time;
    @Column(nullable = false)
    private boolean isAvailable = true;
    @Column(nullable = true)
    private String usernameClient;
    @Column(nullable = true)
    private long chatId;

    public SlotModel(StaffModel staffModel, LocalDate date, LocalTime time) {
        this.staffModel = staffModel;
        this.date = date;
        this.time = time;
    }

    @Override
    public String toString() {
        return "Запись: " + staffModel.getProfession().getProfession_type() + "\n"
                + "Время: " + date + " " + time + "\n"
                + "Мастер: " + staffModel.getFirstName() + " " + staffModel.getLastName();
    }
}
