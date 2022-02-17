package com.me.admissionss.repository;

import com.me.admissionss.entity.Faculty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByName(String name);

    void deleteById(Long id);

    Page<Faculty> findAll(Pageable pageable);

    @Modifying
    @Query("update Faculty f set f.finalized=1 where f.id=?1")
    @Transactional
    int finalizeFaculty(Long id);
}
