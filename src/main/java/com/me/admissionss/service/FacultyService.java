package com.me.admissionss.service;

import com.me.admissionss.entity.Faculty;
import com.me.admissionss.repository.FacultyRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final static Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

    public Faculty save(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty findByName(String name) {
        return facultyRepository.findByName(name).orElseThrow(() ->
                new IllegalArgumentException("Faculty with name: " + name + " not found"));
    }

    public Page<Faculty> findAll(Integer pageNo, Integer pageSize, String order, String sortBy) {
        Sort.Direction direction = order.equals("desc") ? Sort.Direction.DESC : DEFAULT_SORT_DIRECTION;
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(direction, sortBy));

        return facultyRepository.findAll(pageable);
    }

    public void deleteById(Long id) {
        facultyRepository.deleteById(id);
    }

    public void finalizeFaculty(Faculty faculty) {
        facultyRepository.finalizeFaculty(faculty.getId());
    }

    public Boolean isFacultyAlreadyExistsByName(String name) {
        return facultyRepository.findByName(name).isPresent();
    }
}
