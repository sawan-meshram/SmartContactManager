package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class MyConfig extends WebSecurityConfigurerAdapter{
	
	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(this.passwordEncoder());
		
		return daoAuthenticationProvider;
	}

	
	
	//Configure methods...
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
//		super.configure(auth);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests()
			.antMatchers("/admin/**").hasRole("ADMIN") //access admin related urls that has role of admin
			.antMatchers("/user/**").hasRole("USER") //access user related urls that has role of user
//			.antMatchers("/**").permitAll().and().formLogin().and().csrf().disable();  //open its system login page
//			.antMatchers("/**").permitAll().and().formLogin().loginPage("/signin").and().csrf().disable(); //added here ur own login page
			.antMatchers("/**").permitAll().and().formLogin()
			.loginPage("/signin")  //the custom login page
			.loginProcessingUrl("/dologin") //the url to submit the username and password to
			.defaultSuccessUrl("/user/index") //the landing page after a successful login
//			.failureUrl("/login-fail")  //user can send failure on custom fail page url, otherwise, failure occur in page then login page will open
			.and().csrf().disable(); //added here ur own login page
	}
	
	
}
