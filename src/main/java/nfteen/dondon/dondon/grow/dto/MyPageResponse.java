package nfteen.dondon.dondon.grow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class MyPageResponse {
    private MyInfoResponse myInfo;
    private DondonInfoResponse latestDondon;
}
