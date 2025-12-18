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
            return null;
        }

        String idToken = auth.substring(7);
        return googleTokenVerifier.verify(idToken);
    }

    @GetMapping("")
    public MyPageResponse getMyPageInfo(HttpServletRequest request) {
        GoogleUser user = getUserFromToken(request);
        if (user == null) {
            return null;
        }
        return growService.getMyPageInfo(user);
    }

    @GetMapping("/adult")
    public List<MyInfoResponse.AdultDondonResponse> getGraduatedDonDons(HttpServletRequest request) {
        GoogleUser user = getUserFromToken(request);
        if (user == null) {
            return List.of();
        }
        return growService.getGraduatedDonDons(user.getId());
    }

    @PostMapping("/graduate")
    public ResponseEntity<DondonInfo> graduateDondon(HttpServletRequest request) {
        GoogleUser user = getUserFromToken(request);

        MyInfo info = myInfoRepository.findByUserId(user.getId())
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
        boolean liked = likesService.saveLike(user.getId(), body.getTargetId(), body.getDescription());

        return ResponseEntity.ok(new LikeResponse(liked));
    }

    @GetMapping("/likes")
    public ResponseEntity<List<LikesResponse>> getLikes(
            HttpServletRequest request, @RequestParam String des) {
        {
            GoogleUser user = getUserFromToken(request);
            if (user == null) {
                return ResponseEntity.ok(List.of());
            }
            return ResponseEntity.ok(likesService.getLikes(user.getId(), des));

        }
    }

    @GetMapping("/prizes")
    public ResponseEntity<List<PrizeResponse>> getPrizes(HttpServletRequest request) {
        GoogleUser user = getUserFromToken(request);
        if (user == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(growService.getPrizes(user.getId()));
    }

    @GetMapping("/shop")
    public ResponseEntity<List<AccessaryResponse>> getAllAccessaries(HttpServletRequest request) {
        List<AccessaryResponse> accessaries = shopService.getAllAccessaries();
        return ResponseEntity.ok(accessaries);
    }

    @PostMapping("/shop/buy")
    public ResponseEntity<BuyAccResponse> buyAccessary(HttpServletRequest request, @RequestBody BuyAccRequest body) {
        GoogleUser user = getUserFromToken(request);

        BuyAccResponse response = shopService.buyAcc(user.getId(), body.getAccId());
        return ResponseEntity.ok(response);
    }

}
