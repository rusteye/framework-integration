package org.free.resserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableResourceServer
public class ResServerConfig extends ResourceServerConfigurerAdapter {
	@Autowired
    TokenStore tokenStore;
	
//	@Bean
//	RemoteTokenServices tokenServices() {
//		RemoteTokenServices services = new RemoteTokenServices();
//		services.setCheckTokenEndpointUrl("http://localhost:8080/oauth/check_token");
//		services.setClientId("javaboy");
//		services.setClientSecret("123");
//		return services;
//	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId("res1").tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/admin/**").hasRole("admin").anyRequest().authenticated();
	}
}