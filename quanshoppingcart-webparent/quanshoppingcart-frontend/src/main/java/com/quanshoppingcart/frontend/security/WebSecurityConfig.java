package com.quanshoppingcart.frontend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private DatabaseLoginSuccessHandler databaseLoginHandler;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomerUserDetailsService();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.anyRequest().permitAll()
				.and()
				.formLogin()
					.loginPage("/login")
					.usernameParameter("email")
					.successHandler(databaseLoginHandler)//chuyển hướng đến trang bắt đăng nhập hoặc trang index
					.permitAll()
				.and()
				.logout().permitAll()
				.and()
				.rememberMe()
					.key("1234567890_aBcDeFgHiJkLmNoPqRsTuVwXyZ")
					.tokenValiditySeconds(14 * 24 * 60 * 60)
				.and()
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
	/*
	SessionCreationPolicy.ALWAYS sẽ bắt buộc Spring Security luôn tạo mới một phiên làm việc mới(session) cho user mỗi khi request được gửi đến controller.
	Có nghĩa là nếu user đã có một phiên làm việc(session) đang hoạt động, nó sẽ bị hủy và một phiên mới(session) sẽ được tạo ra.
	*/
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**");
	}

}
