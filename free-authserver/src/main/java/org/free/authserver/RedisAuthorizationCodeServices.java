package org.free.authserver;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Component;

@Component
public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

	@Autowired
	private RedisTemplate<Object, Object> redisTemplate;

	@Override
	protected void store(String code, OAuth2Authentication authentication) {
		this.redisTemplate.opsForHash().put("auth_code", code, authentication);
	}

	@Override
	public OAuth2Authentication remove(String code) {
		Map<Object, Object> authorizationCodeStore = redisTemplate.opsForHash().entries("auth_code");
		redisTemplate.opsForHash().delete("auth_code", code);
		return (OAuth2Authentication) authorizationCodeStore.get(code);
	}

}