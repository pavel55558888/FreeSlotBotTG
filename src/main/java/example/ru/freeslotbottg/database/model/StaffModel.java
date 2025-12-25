package example.ru.freeslotbottg.database.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
public class StaffModel {
    @Id
    @GeneratedValue
    private long id;
    @OneToOne
    private ProfessionModel profession;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;

    public StaffModel(ProfessionModel profession, String firstName, String lastName) {
        this.profession = profession;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
