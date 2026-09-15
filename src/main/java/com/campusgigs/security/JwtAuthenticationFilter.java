package com.campusgigs.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Roda uma vez por requisição: se vier "Authorization: Bearer <token>" válido,
// autentica a requisição no SecurityContext. Se não vier token, ou o token for
// inválido/expirado, a requisição segue anônima — quem decide se isso é um problema
// é o authorizeHttpRequests do SecurityConfig (via RestAuthenticationEntryPoint, 401).
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIXO_BEARER = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String cabecalho = request.getHeader("Authorization");
        if (cabecalho == null || !cabecalho.startsWith(PREFIXO_BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = cabecalho.substring(PREFIXO_BEARER.length());

        try {
            String email = jwtService.extrairEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Papel/authorities vêm sempre frescos do banco (não do claim do token),
                // para refletir imediatamente uma eventual mudança de papel do usuário.
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException | UsernameNotFoundException | IllegalArgumentException ex) {
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
