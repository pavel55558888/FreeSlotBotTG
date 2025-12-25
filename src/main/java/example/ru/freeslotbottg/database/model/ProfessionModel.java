package example.ru.freeslotbottg.database.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "profession")
@Getter
@Setter
@NoArgsConstructor
public class ProfessionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String profession_type;

    public ProfessionModel(String profession_type) {
        this.profession_type = profession_type;
    }

    @Override
    public String toString() {
        return profession_type;
    }
}
