package example.ru.freeslotbottg.database.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
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
    private StaffModel staff;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private ClientModel client;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    private LocalTime time;
    @Column(nullable = false)
    private boolean isAvailable;
    @Column(nullable = true)
    private boolean pushNotify;

    public SlotModel(StaffModel staff, LocalDate date, LocalTime time) {
        this.staff = staff;
        this.date = date;
        this.time = time;
        this.isAvailable = true;
        this.pushNotify = false;
    }

    @Override
    public String toString() {
        return "Запись: " + staff.getProfession().getProfession_type() + "\n"
                + "Время: " + date + " " + time + "\n"
                + "Мастер: " + staff.getFirstName() + " " + staff.getLastName();
    }
}
