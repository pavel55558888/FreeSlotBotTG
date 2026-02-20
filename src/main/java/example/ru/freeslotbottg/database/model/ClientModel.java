package example.ru.freeslotbottg.database.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
public class ClientModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToMany
    @JoinColumn(name = "client_id")
    private List<StaffModel> staffModelList;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private long chatId;
    @Column(nullable = true)
    private LocalDate lastPushNotifyDate;
    @Column(nullable = true)
    private LocalTime lastPushNotifyTime;

    public ClientModel(List<StaffModel> staffModelList, String username, long chatId) {
        this.staffModelList = staffModelList;
        this.username = username;
        this.chatId = chatId;
    }
}
