package com.me.admissionss.controller;

import com.itextpdf.text.DocumentException;
import com.me.admissionss.entity.User;
import com.me.admissionss.entity.UserRole;
import com.me.admissionss.service.FacultyRegistrationService;
import com.me.admissionss.service.UserService;
import com.me.admissionss.util.UserPDFExporter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final FacultyRegistrationService facultyRegistrationService;

    @GetMapping
    public String userList(@NonNull Model model) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("adminRole", UserRole.ADMIN);

        return "userList";
    }

    @GetMapping("/block/{user}")
    public String userBlock(@PathVariable @NonNull User user, Model model) {
        userService.blockUser(user.getId());

        return "redirect:/user";
    }

    @GetMapping("/unblock/{user}")
    public String userUnblock(@PathVariable @NonNull User user, Model model) {
        userService.unblockUser(user.getId());

        return "redirect:/user";
    }

    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    @GetMapping("/profile")
    public String myProfile(@AuthenticationPrincipal User user, Model model) {

        model.addAttribute("facultyRegistration", facultyRegistrationService.findAllFacultyRegistrations(user));
        model.addAttribute("user", user);
        model.addAttribute("myAdminProfile", user.getRoles().contains(UserRole.ADMIN));

        return "userProfile";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/profile/{user}")
    public String userProfile(@PathVariable User user, Model model) {

        model.addAttribute("facultyRegistration", facultyRegistrationService.findAllFacultyRegistrations(user));
        model.addAttribute("user", user);
        model.addAttribute("myAdminProfile", false);

        return "userProfile";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<User> listUsers = userService.findAllUsers();

        UserPDFExporter exporter = new UserPDFExporter(listUsers);
        exporter.export(response);
    }
}
