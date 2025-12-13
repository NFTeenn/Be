package nfteen.dondon.dondon.grow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DondonInfoResponse {
    private int gen;
    private String nickname;
    private int level;
    private LocalDate enterDate;
    private LocalDate graduationDate;
    private int style;
    private Long accId;
}