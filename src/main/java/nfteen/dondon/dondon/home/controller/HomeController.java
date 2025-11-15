package nfteen.dondon.dondon.home.controller;

import nfteen.dondon.dondon.home.dto.HomeRequest;
import nfteen.dondon.dondon.home.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private HomeService homeService;

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

    @GetMapping("/word")
    public Object showWord(@RequestBody HomeRequest request) {
        try {
            if (request.getToken() == null || request.getToken().isEmpty()) {
                return "token이 없습니다.";
            }
            return homeService.showWords(request);
        } catch (Exception e) {
            e.printStackTrace();
            return "오류 발생";
        }
    }

}
