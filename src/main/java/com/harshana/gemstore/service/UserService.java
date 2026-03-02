package com.harshana.gemstore.service;

import com.harshana.gemstore.entity.Role;
import com.harshana.gemstore.entity.User;
import com.harshana.gemstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.BUYER);

        return userRepository.save(user);
    }

}
