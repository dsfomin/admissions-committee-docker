package com.me.admissionss.entity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "usr")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Email shouldn't be empty")
    @Email(message = "Incorrect email type")
    private String email;

    @NotNull(message = "Password can't be empty")
    @NotBlank(message = "Password is mandatory")
    private String password;

    private Boolean active;

    @Min(value = 0, message = "{validation.user.note}")
    @Max(value = 12, message = "{validation.user.note}")
    @NotNull(message = "Note is mandatory")
    private Double averageSchoolNote;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    Set<FacultyRegistration> selectedFaculties;

    @ElementCollection(targetClass = Double.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_subjects", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyEnumerated(EnumType.STRING)
    private Map<Subject, Double> notes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getUsername() {
        return email;
    }

    public void setUsername(String username) {
        email = username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public String toString() {
        return "[" +
                "id = " + id + ", " +
                "email = " + email + ", " +
                "active = " + active + ", " +
                "selected faculties = " + selectedFaculties +
                "]";
    }
}