package com.claudev.agenda.service;

import com.claudev.agenda.entity.User;
import com.claudev.agenda.mapper.UserMapper;
import com.claudev.agenda.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // utile per i test iniziali
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // per il login con OAuth2
    public User processOAuthPostLogin(String email, String firstname, String lastname) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = userMapper.toEntityFromOAuth2(email, firstname, lastname);
                    return userRepository.save(newUser);
                });
    }
}
