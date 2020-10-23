package org.free.web;

import java.io.PrintWriter;

import javax.sql.DataSource;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	DataSource dataSource;
	@Autowired
	UserService userService;
	
	@Bean
	PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/js/**", "/css/**", "/images/**");
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	    auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
	}
	
//	@Bean
//	protected UserDetailsService userDetailsService() {
//	    JdbcUserDetailsManager manager = new JdbcUserDetailsManager();
//	    manager.setDataSource(dataSource);
//	    if (!manager.userExists("admin")) {
//	        manager.createUser(User.withUsername("admin").password("123").roles("admin").build());
//	    }
//	    if (!manager.userExists("user")) {
//	        manager.createUser(User.withUsername("user").password("123").roles("user").build());
//	    }
//	    return manager;
//	}
	
	@Bean
	RoleHierarchy roleHierarchy() {
	    RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
	    hierarchy.setHierarchy("ROLE_admin > ROLE_user");
	    return hierarchy;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/admin/**").hasRole("admin")
	        .antMatchers("/user/**").hasRole("user")
			.anyRequest()
			.authenticated()
			.and()
			.formLogin()
			.loginPage("/login.html")
			.loginProcessingUrl("/login")
			.usernameParameter("username")
			.passwordParameter("password")
//			.defaultSuccessUrl("/hello")
			.successHandler((req, resp, authentication) -> {
			    Object principal = authentication.getPrincipal();
			    resp.setContentType("application/json;charset=utf-8");
			    PrintWriter out = resp.getWriter();
			    out.write(new ObjectMapper().writeValueAsString(principal));
			    out.flush();
			    out.close();
			})
			.failureHandler((req, resp, e) -> {
			    resp.setContentType("application/json;charset=utf-8");
			    PrintWriter out = resp.getWriter();
			    out.write(e.getMessage());
			    out.flush();
			    out.close();
			})
			.permitAll()
			.and()
			.logout()
			.logoutUrl("/logout")
			.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
			.logoutSuccessUrl("/login.html")
			.deleteCookies()
			.clearAuthentication(true)
			.invalidateHttpSession(true)
			.logoutSuccessHandler((req, resp, authentication) -> {
			    resp.setContentType("application/json;charset=utf-8");
			    PrintWriter out = resp.getWriter();
			    out.write("注销成功");
			    out.flush();
			    out.close();
			})
			.permitAll()
			.and()
			.csrf()
			.disable()
			.exceptionHandling()
			.authenticationEntryPoint((req, resp, authException) -> {
			            resp.setContentType("application/json;charset=utf-8");
			            PrintWriter out = resp.getWriter();
			            out.write("尚未登录，请先登录");
			            out.flush();
			            out.close();
			        }
			);
	}
}