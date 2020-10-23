package org.free.authserver;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.InMemoryAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@EnableAuthorizationServer
@Configuration
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter {
	@Autowired
	TokenStore tokenStore;
	@Autowired
	ClientDetailsService clientDetailsService;
	@Autowired
	DataSource dataSource;
	@Autowired
	JwtAccessTokenConverter jwtAccessTokenConverter;
	@Autowired
	CustomAdditionalInformation customAdditionalInformation;
	
	/**
	 * 客户端数据配置在数据库中
	 * @return
	 */
	@Bean
	@Primary
	ClientDetailsService jdbcClientDetailsService() {
	    return new JdbcClientDetailsService(dataSource);
	}

	@Bean
	AuthorizationServerTokenServices tokenServices() {
	    DefaultTokenServices services = new DefaultTokenServices();
	    services.setClientDetailsService(jdbcClientDetailsService());
	    services.setSupportRefreshToken(true);
	    services.setTokenStore(tokenStore);
	    // 数据库中已经配置
//		services.setAccessTokenValiditySeconds(60 * 60 * 2);
//		services.setRefreshTokenValiditySeconds(60 * 60 * 24 * 3);
	    TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
	    tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtAccessTokenConverter, customAdditionalInformation));
	    services.setTokenEnhancer(tokenEnhancerChain);
	    return services;
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.checkTokenAccess("permitAll()").allowFormAuthenticationForClients();
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//		clients.inMemory().withClient("javaboy").secret(new BCryptPasswordEncoder().encode("123")).resourceIds("res1")
//				.authorizedGrantTypes("authorization_code", "refresh_token").scopes("all")
//				.redirectUris("http://localhost:8082/index.html");
		
		// 保存在数据库中oauth_client_details表
		clients.withClientDetails(jdbcClientDetailsService());
	}

	@Bean
	AuthorizationCodeServices authorizationCodeServices() {
		return new RedisAuthorizationCodeServices();
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authorizationCodeServices(authorizationCodeServices()).tokenServices(tokenServices());
	}
}