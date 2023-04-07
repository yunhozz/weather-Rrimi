package com.authservice.application;

import com.authservice.application.exception.EmailDuplicateException;
import com.authservice.application.exception.PasswordDifferentException;
import com.authservice.application.exception.UserNotFoundException;
import com.authservice.application.provider.JwtProvider;
import com.authservice.application.provider.RedisProvider;
import com.authservice.common.enums.Provider;
import com.authservice.common.enums.Role;
import com.authservice.common.security.session.UserPrincipal;
import com.authservice.domain.Address;
import com.authservice.domain.User;
import com.authservice.dto.request.JoinRequestDto;
import com.authservice.dto.request.LoginRequestDto;
import com.authservice.dto.response.JwtTokenResponseDto;
import com.authservice.dto.response.ProfileResponseDto;
import com.authservice.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RedisProvider redisProvider;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public ProfileResponseDto join(JoinRequestDto joinRequestDto) {
        if (userRepository.existsByEmail(joinRequestDto.getEmail())) {
            throw new EmailDuplicateException();
        }

        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .email(joinRequestDto.getEmail())
                .password(encoder.encode(joinRequestDto.getPassword()))
                .name(joinRequestDto.getName())
                .address(new Address(joinRequestDto.getParentRegion(), joinRequestDto.getChildRegion()))
                .roles(Set.of(Role.USER))
                .provider(Provider.LOCAL)
                .build();
        userRepository.save(user);
        return new ProfileResponseDto(user);
    }

    @Transactional(readOnly = true)
    public JwtTokenResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(UserNotFoundException::new);
        if (!user.isPasswordEqualsWith(encoder.encode(loginRequestDto.getPassword()))) {
            throw new PasswordDifferentException();
        }

        JwtTokenResponseDto jwtTokenResponseDto = jwtProvider.createJwtToken(loginRequestDto.getEmail(), user.getRoles());
        redisProvider.setData(user.getEmail(), jwtTokenResponseDto.getRefreshToken(), Duration.ofMillis(jwtTokenResponseDto.getRefreshTokenValidTime()));
        return jwtTokenResponseDto;
    }

    @Transactional(readOnly = true)
    public JwtTokenResponseDto tokenReissue(String refreshToken) {
        Authentication authentication = jwtProvider.getAuthentication(refreshToken);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        JwtTokenResponseDto jwtTokenResponseDto = jwtProvider.createJwtToken(userPrincipal.getEmail(), userPrincipal.getRoles());
        redisProvider.updateData(userPrincipal.getEmail(), jwtTokenResponseDto.getRefreshToken(), Duration.ofMillis(jwtTokenResponseDto.getRefreshTokenValidTime()));
        return jwtTokenResponseDto;
    }

    @Transactional(readOnly = true)
    public void logout(String token) {
        Authentication authentication = jwtProvider.getAuthentication(token);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        redisProvider.deleteData(userPrincipal.getEmail());
        redisProvider.setData(token, RedisProvider.LOGOUT_STATUS, Duration.ofMinutes(10));
    }
}