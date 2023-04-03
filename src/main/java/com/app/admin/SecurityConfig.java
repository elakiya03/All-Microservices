package com.app.admin;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthFilter;
	
	@Autowired
	private AuthenticationProvider authenticationProvider;
	
    @Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
			.csrf()
			.disable()
			.authorizeHttpRequests()
			.requestMatchers("/admincontroller/*")
				.permitAll()
			.requestMatchers("/employees/*","/employees/**")
				.hasAnyAuthority(Role.EMPLOYEE.toString(),Role.ADMIN.toString())
			.requestMatchers("/managers/*","/managers/**")
				.hasAnyAuthority(Role.MANAGER.toString(),Role.ADMIN.toString())
			.requestMatchers("/admin/*","/admin/**","/departments/*","/departments/*")
				.hasAnyAuthority(Role.ADMIN.toString())			
			.requestMatchers("/admin/*","/employees/**","employees/*","/managers/**","/managers/*","/departments/**","/departments/*")
			.authenticated()
			.and()
			.cors()
			.and().rememberMe()
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	
}

/*
 * @Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
			.csrf()
			.disable()
			.authorizeHttpRequests()
			.requestMatchers("/admincontroller/*")
			.permitAll()
			
			.anyRequest()
			//.hasRole(Role.ADMIN.toString())
			.authenticated()
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			.and()
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
 */
