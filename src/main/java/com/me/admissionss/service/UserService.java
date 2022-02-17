package com.me.admissionss.service;

import com.me.admissionss.entity.User;
import com.me.admissionss.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("User with email: " + email + " not found"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new IllegalArgumentException("User with email: " + email + " not found"));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public void blockUser(Long id) {
        userRepository.blockUser(id);
    }

    public void unblockUser(Long id) {
        userRepository.unblockUser(id);
    }

    public Boolean isUserAlreadyExists(User user) {
        return userRepository.findByEmail(user.getEmail()).isPresent();
    }
}
