package com.authservice.dto.response;

import com.authservice.common.enums.Provider;
import com.authservice.common.enums.Role;
import com.authservice.domain.Address;
import com.authservice.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
public class ProfileResponseDto {

    private String userId;
    private String email;
    private String name;
    private Address address;
    private Set<Role> roles;
    private Provider provider;

    public ProfileResponseDto(User user) {
        userId = user.getUserId();
        email = user.getEmail();
        name = user.getName();
        address = user.getAddress();
        roles = user.getRoles();
        provider = user.getProvider();
    }
}