package nfteen.dondon.dondon.grow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nfteen.dondon.dondon.auth.entity.GoogleUser;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_prize")
public class UserPrize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private GoogleUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Prize prize;

    @Column(nullable = false)
    private boolean achieved;
}
