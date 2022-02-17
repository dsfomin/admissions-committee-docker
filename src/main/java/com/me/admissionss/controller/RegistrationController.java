package com.me.admissionss.controller;

import com.me.admissionss.entity.User;
import com.me.admissionss.entity.UserRole;
import com.me.admissionss.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Set;

@Slf4j
@Controller
@AllArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/registration")
    public String registration(User user) {
        return "registration";
    }

    @Transactional
    @PostMapping("/registration")
    public String addUser(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }

        if (userService.isUserAlreadyExists(user)) {
            model.addAttribute("message", "User with such email already exists!");
            return "registration";
        }

        User newUser = User.builder()
                .active(true)
                .roles(Set.of(UserRole.USER))
                .password(passwordEncoder.encode(user.getPassword()))
                .averageSchoolNote(user.getAverageSchoolNote())
                .email(user.getEmail())
                .build();

        userService.saveUser(newUser);
        log.info("Created user --- " + newUser);

        return "redirect:/login";
    }
}