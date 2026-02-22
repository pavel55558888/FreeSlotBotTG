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
    private boolean isAvailable;
    private String usernameClient;
    private String firstNameClient;
    private String lastNameClient;
    private long chatId;
    private boolean pushNotify;

    public SlotModel(StaffModel staffModel, LocalDate date, LocalTime time) {
        this.staffModel = staffModel;
        this.date = date;
        this.time = time;
        this.isAvailable = true;
        this.pushNotify = false;
    }

    @Override
    public String toString() {
        return "Запись: " + staffModel.getProfession().getProfession_type() + "\n"
                + "Время: " + date + " " + time + "\n"
                + "Мастер: " + staffModel.getFirstName() + " " + staffModel.getLastName();
    }
}
