package nfteen.dondon.dondon.home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class ShowWordResponse {
    private List<String> words;
}
