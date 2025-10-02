package my_project.interceptor;

import io.jsonwebtoken.Claims;
import my_project.util.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Claims claims = JwtUtil.parseToken(token);
                request.setAttribute("claims", claims);
                return true;
            } catch (Exception e) {
                response.setStatus(401);
                return false;
            }
        }
        response.setStatus(401);
        return false;
    }
}
