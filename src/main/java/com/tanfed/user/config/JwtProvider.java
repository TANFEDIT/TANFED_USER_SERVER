package com.tanfed.user.config;

import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

	private static Logger logger = LoggerFactory.getLogger(JwtProvider.class);

	static SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

	public static String generateToken(Authentication auth) {
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

		logger.info("inside generate token{}", authorities);

		String roles = populateAuthorities(authorities);
		LocalDateTime tomorrowMidnight = LocalDate.now().plusDays(1).atStartOfDay();
		ZoneId zoneId = ZoneId.systemDefault();
		Date expirationDate = Date.from(tomorrowMidnight.atZone(zoneId).toInstant());

		String jwt = Jwts.builder().setIssuedAt(new Date()).setExpiration(expirationDate).claim("empId", auth.getName())
				.claim("authorities", roles).claim("lastRequestTime", System.currentTimeMillis()).signWith(key)
				.compact();
		return jwt;
	}

	public static String getEmailFromJwtToken(String jwt) {
		jwt = jwt.substring(7);
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

		String email = String.valueOf(claims.get("empId"));
		return email;
	}

	public static String getRolesFromJwt(String jwt) {
		jwt = jwt.substring(7);
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();

		String email = String.valueOf(claims.get("authorities"));
		return email;
	}

	public static LocalDateTime getExpiryFromJwt(String jwt) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt).getBody();
		return claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	public static String populateAuthorities(Collection<? extends GrantedAuthority> collection) {
		Set<String> auths = new HashSet<String>();

		for (GrantedAuthority authority : collection) {
			auths.add(authority.getAuthority());
		}
		return String.join(",", auths);
	}

}
