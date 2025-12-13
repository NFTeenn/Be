package nfteen.dondon.dondon.grow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikesResponse {
    private Long targetId;
    private String type;
    private boolean liked;

    @Getter
    @AllArgsConstructor
    public static class AccessaryResponse {
        private Long id;
        private String name;
        private String description;
        private int price;
    }
}