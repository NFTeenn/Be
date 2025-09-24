package nfteen.dondon.dondon.oauth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        try {
            ResponseCookie deleteCookie = ResponseCookie.from("access_token", "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0) // 즉시 삭제
                    .sameSite("None")
                    .build();
            response.addHeader("Set-Cookie", deleteCookie.toString());

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Server Error");
            errorResponse.put("message", "로그아웃 처리 중 오류가 발생했습니다");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

