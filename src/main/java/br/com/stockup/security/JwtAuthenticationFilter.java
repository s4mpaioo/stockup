//package br.com.stockup.security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * Filtro que intercepta todas as requisições HTTP.
// * Extrai o token JWT do header Authorization.
// * Valida o token e estabelece a autenticação no SecurityContext.
// */
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    /*
//    @Autowired
//    private JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//        try {
//            // Extrai o token do header Authorization
//            String token = extractTokenFromRequest(request);
//
//            if (token != null && jwtTokenProvider.validateToken(token)) {
//                // Token é válido: extrai dados e estabelece autenticação
//                Long userId = jwtTokenProvider.getUserIdFromJwt(token);
//                String email = jwtTokenProvider.getEmailFromJwt(token);
//
//                // Cria um objeto de autenticação
//                Authentication auth = new UsernamePasswordAuthenticationToken(
//                        email,
//                        null,
//                        null  // authorities (permissões) vazias por enquanto
//                );
//
//                // Estabelece no SecurityContext (disponível para a requisição)
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//        } catch (Exception ex) {
//            // Token inválido ou expirado: continua sem autenticação
//            // O controlador vai rejeitar se o endpoint requer autenticação
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    /**
//     * Extrai o token do header Authorization.
//     * Formato esperado: "Bearer <token>"
//     */
//    private String extractTokenFromRequest(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            return authHeader.substring(7);  // Remove "Bearer "
//        }
//
//        return null;
//    }
//}
//
