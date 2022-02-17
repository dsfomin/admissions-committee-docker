package com.me.admissionss.repository;


import com.me.admissionss.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Modifying
    @Query("update User u set u.active=1 where u.id=?1")
    @Transactional
    int unblockUser(Long id);

    @Modifying
    @Query("update User u set u.active=0 where u.id=?1")
    @Transactional
    int blockUser(Long id);

    @Modifying
    @Query(value = "insert into user_faculty(user_id, faculty_id) values (?1, ?2)",
            nativeQuery = true)
    @Transactional
    void participate(Long id, Long id1);
}