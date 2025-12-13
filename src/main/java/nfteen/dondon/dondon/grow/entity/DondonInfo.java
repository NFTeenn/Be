package nfteen.dondon.dondon.grow.entity;

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
@Table(name = "my_dondon")
public class DondonInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grow_id")
    private MyInfo myInfo;

    private int gen;
    private String nickname;
    private int level;
    private LocalDate enterDate;
    private LocalDate graduationDate;

    private int style;
}
