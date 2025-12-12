package nfteen.dondon.dondon.grow.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdultDondonResponse {
    private Long id;
    private int gen;
    private String nickname;
    private int level;
    private String enterDate;
    private String graduationDate;
    private int style;
}