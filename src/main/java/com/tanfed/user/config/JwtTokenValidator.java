package com.tanfed.user.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tanfed.user.entity.BlackListToken;
import com.tanfed.user.repo.BlackListRepo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtTokenValidator extends OncePerRequestFilter {

//	private static Logger logger = LoggerFactory.getLogger(JwtTokenValidator.class);

	static SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {
		try {
			String jwt = request.getHeader(JwtConstant.JWT_HEADER);
			if (jwt != null) {
				String token = jwt.substring(7);

				boolean isBlocked = blockList().contains(jwt);

				if (isBlocked) {
					throw new BadCredentialsException("Blocked token...");
				}

				Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();

				String email = String.valueOf(claims.get("empId"));
				String authorities = String.valueOf(claims.get("authorities"));

//				logger.info("inside validator{}", authorities);

				List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
				Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, auths);

//				logger.info("List<GrantedAuthority> {}", auths);
//				logger.info("Authentication {}", authentication);

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception e) {
			throw new BadCredentialsException("Invalid token...");
		}

		filterChain.doFilter(request, response);
	}

	private BlackListRepo blockListObj() {
		return SpringContextUtil.getBean(BlackListRepo.class);
	}

	public List<String> blockList() {
		List<String> list = blockListObj().findAll().stream().map(item -> item.getJwt()).collect(Collectors.toList());

		return list == null ? new ArrayList<>() : list;
	}

	public void blockListJwt(String jwt) {
		blockListObj().save(new BlackListToken(null, jwt, JwtProvider.getExpiryFromJwt(jwt.substring(7))));

		blockListObj().findAll().forEach(item -> {
			if (item.getExpiry().isBefore(LocalDateTime.now())) {
				blockListObj().deleteById(item.getId());
			}
		});
//		logger.info("blockedJwt {}", blockList());
	}

}
