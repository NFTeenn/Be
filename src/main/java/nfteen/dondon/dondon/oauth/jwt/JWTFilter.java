package nfteen.dondon.dondon.oauth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import nfteen.dondon.dondon.oauth.dto.CustomOAuth2User;
import nfteen.dondon.dondon.oauth.dto.UserDTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws IOException {

        try{
            String token = extractTokenFromCookie(request);
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            if (jwtUtil.isExpired(token)) {
                sendUnauthorized(response, "JWT token is expired");
                return;
            }

            String email = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);

            UserDTO userDTO = new UserDTO();
            userDTO.setEmail(email);
            userDTO.setRole(role);
            userDTO.setName(email);

            CustomOAuth2User customUser = new CustomOAuth2User(userDTO);
            Authentication authToken = new UsernamePasswordAuthenticationToken(
                    customUser, null, customUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);

            System.out.println("Authentication set successfully");
            filterChain.doFilter(request, response);
            System.out.println("[JWTFilter] After chain: " +
                SecurityContextHolder.getContext().getAuthentication());
        } catch (Exception e){
            sendUnauthorized(response, "Invalid JWT token: " + e.getMessage());
        }
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie cookie : request.getCookies()) {
            if ("access_token".equals(cookie.getName()) && cookie.getValue() != null) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
