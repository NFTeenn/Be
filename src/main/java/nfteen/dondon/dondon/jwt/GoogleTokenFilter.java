package nfteen.dondon.dondon.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import nfteen.dondon.dondon.service.GoogleTokenVerifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GoogleTokenFilter extends OncePerRequestFilter {

    private final GoogleTokenVerifier googleTokenVerifier;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                var googleUser = googleTokenVerifier.verify(token);
                if(googleUser) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    googleUser, null, List.of()
                            );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }catch (Exception e){
                logger.error("Google Token 검증 실패",e);
            }
        }
        filterChain.doFilter(request, response);
    }
}

