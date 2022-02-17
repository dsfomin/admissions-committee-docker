package com.me.admissionss.entity;


import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacultyRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @ElementCollection
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Subject, Double> subjects_notes;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;

    public Double getAverageExamNote() {
        return subjects_notes.values().stream().mapToDouble(x -> x).average().orElse(0.0);
    }

    public Double getUserAverageSchoolNote() {
        return user.getAverageSchoolNote();
    }
}