package nfteen.dondon.dondon.grow.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.auth.entity.GoogleUser;
import nfteen.dondon.dondon.auth.service.GoogleTokenVerifier;
import nfteen.dondon.dondon.grow.dto.*;
import nfteen.dondon.dondon.grow.entity.*;
import nfteen.dondon.dondon.grow.repository.MyInfoRepository;
import nfteen.dondon.dondon.grow.service.GrowService;
import nfteen.dondon.dondon.grow.service.LikesService;
import nfteen.dondon.dondon.grow.service.ShopService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/grow")
@RequiredArgsConstructor
public class GrowController {
    private final GrowService growService;
    private final LikesService likesService;
    private final ShopService shopService;
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
    public List<MyInfoResponse.AdultDondonResponse> getGraduatedDonDons(@RequestParam Long userId) {
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
    public ResponseEntity<ChangeNameResponse> changeDondonName(
            HttpServletRequest request,
            @RequestBody ChangeNameRequest body
    ) {
        GoogleUser user = getUserFromToken(request);
        growService.changeDondonName(user.getId(), body.getNickname());
        return ResponseEntity.ok(new ChangeNameResponse(body.getNickname()));
    }

    @PostMapping("/likes")
    public ResponseEntity<LikeResponse> likeDondon(
            HttpServletRequest request,
            @RequestBody LikeRequest body) {
        GoogleUser user = getUserFromToken(request);
        boolean liked = likesService.saveLike(user.getId(), body.getTargetId(), body.getType());

        return ResponseEntity.ok(new LikeResponse(liked));
    }

    @GetMapping("/likes")
    public ResponseEntity<List<LikesResponse>> getLikes(
            HttpServletRequest request,
            @RequestParam(required = false) TypeName type,
            @RequestParam(required = false) Long targetId) {
        GoogleUser user = getUserFromToken(request);
        List<LikesResponse> list = likesService.getLikes(user.getId(), type, targetId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/prizes")
    public ResponseEntity<List<PrizeResponse>> getPrizes(HttpServletRequest request) {
        GoogleUser user = getUserFromToken(request);
        List<PrizeResponse> prizes = growService.getPrizes(user.getId());
        return ResponseEntity.ok(prizes);
    }

    @GetMapping("/shop")
    public ResponseEntity<List<LikesResponse.AccessaryResponse>> getAllAccessaries(HttpServletRequest request) {
        GoogleUser user = getUserFromToken(request);
        List<LikesResponse.AccessaryResponse> accessaries = shopService.getAllAccessaries();
        return ResponseEntity.ok(accessaries);
    }

    @PostMapping("/shop/buy")
    public ResponseEntity<BuyAccResponse> buyAccessary(HttpServletRequest request, @RequestBody BuyAccRequest body) {
        GoogleUser user = getUserFromToken(request);

        BuyAccResponse response = shopService.buyAcc(user.getId(), body.getAccId());
        return ResponseEntity.ok(response);
    }

}
