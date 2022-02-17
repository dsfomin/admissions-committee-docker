package com.me.admissionss.service;

import com.me.admissionss.entity.Faculty;
import com.me.admissionss.entity.FacultyRegistration;
import com.me.admissionss.entity.User;
import com.me.admissionss.repository.FacultyRegistrationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FacultyRegistrationService {

    private final FacultyRegistrationRepository facultyRegistrationRepository;

    public FacultyRegistration saveFacultyRegistration(FacultyRegistration facultyRegistration) {
        return facultyRegistrationRepository.save(facultyRegistration);
    }

    public FacultyRegistration findFacultyRegistration(Faculty faculty, User user) {
        return facultyRegistrationRepository.findByFacultyAndUser(faculty, user).orElseThrow(() ->
                new IllegalArgumentException("Unknown faculty registration")
        );
    }

    public List<FacultyRegistration> findAllFacultyRegistrations(Faculty faculty) {
        return facultyRegistrationRepository.findByFaculty(faculty);
    }

    public List<FacultyRegistration> findAllFacultyRegistrations(User user) {
        return facultyRegistrationRepository.findByUser(user);
    }
}
