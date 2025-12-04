package nfteen.dondon.dondon.grow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "acc")
public class UserAcc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "grow_id")
    private MyInfo myInfo;

    @ManyToOne
    @JoinColumn(name = "acc_id")
    private Accessary acc;

    private boolean equipped;
}