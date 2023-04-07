package com.authservice.application;

import com.authservice.application.exception.PasswordDifferentException;
import com.authservice.application.exception.PasswordUpdateFailException;
import com.authservice.application.exception.UserNotFoundException;
import com.authservice.domain.User;
import com.authservice.dto.request.AddressRequestDto;
import com.authservice.dto.request.UpdatePasswordRequestDto;
import com.authservice.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public void changePassword(String userId, UpdatePasswordRequestDto updatePasswordRequestDto) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(UserNotFoundException::new);
        if (!user.isPasswordEqualsWith(encoder.encode(updatePasswordRequestDto.getOriginalPw()))) {
            throw new PasswordDifferentException();
        }

        if (!updatePasswordRequestDto.getNewPw().equals(updatePasswordRequestDto.getConfirmPw())) {
            throw new PasswordUpdateFailException();
        }

        user.changePassword(encoder.encode(updatePasswordRequestDto.getNewPw()));
    }

    @Transactional
    public void changeAddress(String userId, AddressRequestDto addressRequestDto) {
        User user = userRepository.findByUserId(userId)
                        .orElseThrow(UserNotFoundException::new);
        user.changeAddress(addressRequestDto.getParent(), addressRequestDto.getChild());
    }
}