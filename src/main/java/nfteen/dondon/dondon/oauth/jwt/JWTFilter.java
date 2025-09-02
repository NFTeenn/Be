package nfteen.dondon.dondon.oauth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.oauth.dto.CustomOAuth2User;
import nfteen.dondon.dondon.oauth.dto.UserDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = null;

        // 요청 쿠키에서 access_token 찾기
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())
                        && cookie.getValue() != null
                        && !cookie.getValue().isEmpty()) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            // 토큰이 없으면 필터 체인 계속 진행
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰 만료 확인
        if (jwtUtil.isExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰에서 사용자 정보 꺼내기
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setRole(role);

        CustomOAuth2User customUser = new CustomOAuth2User(userDTO);
        Authentication authToken = new UsernamePasswordAuthenticationToken(
                customUser, null, customUser.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}
