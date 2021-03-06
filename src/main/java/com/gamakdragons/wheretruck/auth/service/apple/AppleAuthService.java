package com.gamakdragons.wheretruck.auth.service.apple;

import com.gamakdragons.wheretruck.auth.dto.LoginRequestDto;
import com.gamakdragons.wheretruck.auth.dto.LoginResponseDto;
import com.gamakdragons.wheretruck.auth.dto.LogoutRequestDto;
import com.gamakdragons.wheretruck.auth.dto.LogoutResponseDto;
import com.gamakdragons.wheretruck.auth.dto.apple.ApplePublicKeyResponse;
import com.gamakdragons.wheretruck.auth.service.JwtUtil;
import com.gamakdragons.wheretruck.auth.service.OAuth2Service;
import com.gamakdragons.wheretruck.common.IndexUpdateResultDto;
import com.gamakdragons.wheretruck.domain.user.entity.User;
import com.gamakdragons.wheretruck.domain.user.service.UserService;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Qualifier("apple")
@Service
@Slf4j
@RequiredArgsConstructor
public class AppleAuthService implements OAuth2Service {

	private final RestTemplate restTemplate;
	private final UserService userService;

	@Value("${oauth2.provider.apple.public_key_url}")
	private String publicKeyUrl;

	@Override
	public LoginResponseDto login(LoginRequestDto loginRequestDto) {

		String identityToken = loginRequestDto.getAuthToken();
		log.info("identityToken= " + identityToken);

		ApplePublicKeyResponse applePublicKeyResponse = restTemplate.getForObject(publicKeyUrl, ApplePublicKeyResponse.class);
		log.info(applePublicKeyResponse.toString());

		String userId;
		try {
			userId = JwtUtil.parseUserIdFormAppleJwt(identityToken, applePublicKeyResponse);
		} catch(NullPointerException e) {
			log.error(e.getMessage(), e);
			return LoginResponseDto.builder()
									.user(null)
									.jwt(null)
									.build();
		}

		User user = userService.getById(userId);

		if(user != null) {
            log.info("user exists: " + user);
        } else {

			user = new User();
			user.setId(userId);
			user.setNickName(loginRequestDto.getNickName());
			user.setRole(loginRequestDto.getRole());

            IndexUpdateResultDto result = userService.saveUser(user); 
            log.info("user save result: " + result);
        }

		String jwt = JwtUtil.generateToken(userId);
        log.info("jwt=" + jwt);

		return LoginResponseDto.builder()
								.user(user)
								.jwt(jwt)
								.build();
	}

	@Override
	public LogoutResponseDto logout(String userId, LogoutRequestDto logoutRequestDto) {
		// TODO Auto-generated method stub
		return null;
	}
	

	
}
