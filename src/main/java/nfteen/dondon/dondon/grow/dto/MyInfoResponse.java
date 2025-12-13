package nfteen.dondon.dondon.grow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyInfoResponse {
    private String username;
    private int days;
    private int quizStack;
    private int newsStack;
    private int recentGen;
    private int coin;

    @Getter
    @AllArgsConstructor
    public static class AdultDondonResponse {
        private Long id;
        private int gen;
        private String nickname;
        private int level;
        private String enterDate;
        private String graduationDate;
        private int style;
    }
}