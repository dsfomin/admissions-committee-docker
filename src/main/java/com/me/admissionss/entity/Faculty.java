package com.me.admissionss.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;


@Getter
@Setter
@Entity
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer budgetPlaces;
    private Integer contractPlaces;

    @OneToMany(mappedBy = "faculty", cascade = CascadeType.ALL)
    Set<FacultyRegistration> candidates;

    @ElementCollection(targetClass = Subject.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "faculty_subject", joinColumns = @JoinColumn(name = "faculty_id"))
    @Enumerated(EnumType.STRING)
    private Set<Subject> examSubjects;

    private Boolean finalized;

    @Override
    public String toString() {
        return "[" +
                "id = " + id + ", " +
                "name = " + name + ", " +
                "budgetPlaces = " + budgetPlaces + ", " +
                "contractPlaces = " + contractPlaces + ", " +
                "]";
    }

    public boolean hasName(Faculty faculty) {
        return faculty.name != null;
    }
}
