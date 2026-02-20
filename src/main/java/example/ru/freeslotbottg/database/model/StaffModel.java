package example.ru.freeslotbottg.database.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
public class StaffModel {
    @Id
    @GeneratedValue
    private long id;
    @ManyToOne(fetch = FetchType.EAGER)
    private ProfessionModel profession;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String username;
    @Column(nullable = true)
    private long chatId;
    @Column(nullable = true)
    private LocalDate lastActivityAddedSlotDate;

    public StaffModel(ProfessionModel profession, String firstName, String lastName, String username) {
        this.profession = profession;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
