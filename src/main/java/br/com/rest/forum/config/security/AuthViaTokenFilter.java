package br.com.rest.forum.config.security;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.rest.forum.controller.TokenService;
import br.com.rest.forum.modelo.Usuario;
import br.com.rest.forum.repository.UserRepository;

public class AuthViaTokenFilter extends OncePerRequestFilter {

	
	private TokenService tokenService;
	
	private UserRepository userRepository;
	
	
	public AuthViaTokenFilter(TokenService tokenService,UserRepository userRepository) {
		this.tokenService = tokenService;
		this.userRepository=userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
			String token= recuperarToken(request);
			boolean valido=tokenService.isTokenValid(token);
			if (valido) {
				authClient(token);
			}
			filterChain.doFilter(request, response);
	}

	private void authClient(String token) {
		Long idUsuario= tokenService.getIdUsuario(token);
		Usuario usuario=userRepository.findById(idUsuario).get();
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private String recuperarToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		if(token==null||token.isEmpty()||!token.startsWith("Bearer")) {
			return null;
		}
		return token.substring(7,token.length());
	}

}
