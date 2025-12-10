package nfteen.dondon.dondon.grow.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.auth.entity.GoogleUser;
import nfteen.dondon.dondon.auth.service.GoogleTokenVerifier;
import nfteen.dondon.dondon.grow.dto.ChangeNameRequest;
import nfteen.dondon.dondon.grow.dto.MyPageResponse;
import nfteen.dondon.dondon.grow.entity.DondonInfo;
import nfteen.dondon.dondon.grow.entity.MyInfo;
import nfteen.dondon.dondon.grow.repository.MyInfoRepository;
import nfteen.dondon.dondon.grow.service.GrowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grow")
@RequiredArgsConstructor
public class GrowController {
    private final GrowService growService;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final MyInfoRepository myInfoRepository;

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

    @GetMapping("/adult")
    public List<DondonInfo> getGraduatedDonDons(@RequestParam Long userId) {
        return growService.getGraduatedDonDons(userId);
    }

    @PostMapping("/graduate")
    public ResponseEntity<DondonInfo> graduateDondon(@RequestParam Long userId) {
        MyInfo info = myInfoRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 정보 없음"));

        DondonInfo newDondon = growService.graduateAndAdopt(info);

        return ResponseEntity.ok(newDondon);
    }

    @PatchMapping("/nickname")
    public ResponseEntity<Void> changeDondonName(
            HttpServletRequest request,
            @RequestBody ChangeNameRequest body
    ) {
        GoogleUser user = getUserFromToken(request);
        growService.changeDondonName(user.getId(), body.getNickname());
        return ResponseEntity.ok().build();
    }

}
