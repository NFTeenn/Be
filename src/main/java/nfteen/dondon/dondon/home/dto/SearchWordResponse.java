package nfteen.dondon.dondon.home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchWordResponse {
    private String word;
    private String description;
    private String subject;
}
