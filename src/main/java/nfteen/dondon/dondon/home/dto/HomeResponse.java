package nfteen.dondon.dondon.home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class HomeResponse {
    private int day;
    private int level;
    private List<String> mission;
    private int quizCount;
    private String quiz;
    private List<String> a;
    private List<String> words;
    private int result;
    private String content;
}
