package com.me.admissionss.repository;

import com.me.admissionss.entity.Faculty;
import com.me.admissionss.entity.FacultyRegistration;
import com.me.admissionss.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FacultyRegistrationRepository extends JpaRepository<FacultyRegistration, Long> {
    Optional<FacultyRegistration> findByFacultyAndUser(Faculty faculty, User user);

    List<FacultyRegistration> findByFaculty(Faculty faculty);

    List<FacultyRegistration> findByUser(User user);
}

