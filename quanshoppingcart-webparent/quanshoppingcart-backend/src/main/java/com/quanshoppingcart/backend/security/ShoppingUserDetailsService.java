package com.quanshoppingcart.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.quanshoppingcart.common.entity.User;
import com.quanshoppingcart.backend.user.UserRepository;

public class ShoppingUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepo;

	@Override//implements Interface UserDetailsService phải @Override lại phương thức loadUserByUsername, đây là phương thức kiểm tra email và password
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepo.getUserByEmail(email);
		if (user != null) {
			return new ShoppingUserDetails(user);
		}

		throw new UsernameNotFoundException("Could not find user with email: " + email);
	}

}
