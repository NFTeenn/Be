package nfteen.dondon.dondon.home.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "home")
public class Home {
    @Id
    private String email;

    private int day;
    private int level;
    private int quizCount;
    private LocalDate create;

    @Column(columnDefinition = "JSON")
    private String mission;
}
