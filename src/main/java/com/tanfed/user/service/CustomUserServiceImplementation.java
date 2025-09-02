package com.tanfed.user.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tanfed.user.entity.User;
import com.tanfed.user.repo.UserRepository;

@Service
public class CustomUserServiceImplementation implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmpId(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with empId :" + username);
		}

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		user.getRole().forEach(item -> {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + item));
		});

		return new org.springframework.security.core.userdetails.User(user.getEmpId(), user.getPassword(), authorities);
	}

}
