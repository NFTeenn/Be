package nfteen.dondon.dondon.grow.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.auth.entity.GoogleUser;
import nfteen.dondon.dondon.auth.jwt.JWTUtil;
import nfteen.dondon.dondon.auth.service.GoogleTokenVerifier;
import nfteen.dondon.dondon.grow.dto.MyPageResponse;
import nfteen.dondon.dondon.grow.service.GrowService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/grow")
@RequiredArgsConstructor
public class GrowController {
    private final GrowService growService;
    private final GoogleTokenVerifier googleTokenVerifier;

    private GoogleUser getUserFromToken(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new IllegalArgumentException("토큰이 없습니다.");
        }

        String idToken = auth.substring(7);
        GoogleUser user = googleTokenVerifier.verify(idToken);
        if (user == null) {
            throw new IllegalArgumentException("토큰 검증 실패");
        }
        return user;
    }

    @GetMapping("/")
    public MyPageResponse getMyPageInfo(HttpServletRequest request) {
        GoogleUser user = getUserFromToken(request);
        return growService.getMyPageInfo(user);
    }





}
