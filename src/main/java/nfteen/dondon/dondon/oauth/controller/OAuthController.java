package nfteen.dondon.dondon.oauth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class OAuthController {

    @GetMapping("/login")
    @ResponseBody
    public String myAPI() {

        return "login";
    }
}

