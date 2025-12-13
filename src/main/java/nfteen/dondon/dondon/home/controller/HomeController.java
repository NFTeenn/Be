package nfteen.dondon.dondon.home.controller;

import jakarta.servlet.http.HttpServletRequest;
import nfteen.dondon.dondon.auth.entity.GoogleUser;
import nfteen.dondon.dondon.auth.service.GoogleTokenVerifier;
import nfteen.dondon.dondon.home.dto.BasicRequest;
import nfteen.dondon.dondon.home.dto.HomeRequest;
import nfteen.dondon.dondon.home.dto.SearchWordRequest;
import nfteen.dondon.dondon.home.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private HomeService homeService;
    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;

    @PostMapping
    public Object handleHome(@RequestBody HomeRequest request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return "token이 없습니다.";
            }
            return homeService.processHome(request);
        } catch (Exception e) {
            e.printStackTrace();
            return "오류 발생";
        }
    }

    @GetMapping("/words")
    public Object showWord(HttpServletRequest request) {
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                return "token이 없습니다.";
            }

            String idToken = auth.substring(7);
            GoogleUser user = googleTokenVerifier.verify(idToken);
            if(user == null) {
                return "토큰 검증 실패";
            }

            return homeService.showWords(user.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            return "오류 발생";
        }
    }

    @PostMapping("/words/search")
    public Object searchWord(HttpServletRequest request, @RequestBody SearchWordRequest body) {
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                return "token이 없습니다.";
            }
            String idToken = auth.substring(7);
            GoogleUser user = googleTokenVerifier.verify(idToken);
            if(user == null) {
                return "토큰 검증 실패";
            }
            body.setEmail(user.getEmail());
            return homeService.searchWords(body);
        } catch (Exception e) {
            e.printStackTrace();
            return "오류 발생";
        }
    }

    @PostMapping("/news")
    public Object showNews(HttpServletRequest request, @RequestBody BasicRequest body) {
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) {
                return "token이 없습니다.";
            }
            String idToken = auth.substring(7);
            GoogleUser user = googleTokenVerifier.verify(idToken);
            if(user == null) {
                return "토큰 검증 실패";
            }

            body.setEmail(user.getEmail());
            return homeService.showNews(body);
        } catch (Exception e) {
            e.printStackTrace();
            return "오류 발생";
        }
    }

}
