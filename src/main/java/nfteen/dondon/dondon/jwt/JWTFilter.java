package nfteen.dondon.dondon.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if(header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if(jwtUtil.validateToken(token)) {
                var auth = new UsernamePasswordAuthenticationToken("user", null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
                System.out.println("JWT 검증 성공: " + token);
            } else {
                System.out.println("JWT 검증 실패");
            }
        }
        filterChain.doFilter(request, response);
    }
}