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
@Table(name = "grow")
public class MyInfo {
    @Id
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private GoogleUser user;

    private String username;
    private int days;
    private int quizStack;
    private int newsStack;
    private int recentGen;
    private int coin;
}
