package com.me.admissionss.controller;

import com.itextpdf.text.DocumentException;
import com.me.admissionss.entity.Faculty;
import com.me.admissionss.entity.FacultyRegistration;
import com.me.admissionss.entity.Subject;
import com.me.admissionss.entity.User;
import com.me.admissionss.service.FacultyRegistrationService;
import com.me.admissionss.service.FacultyService;
import com.me.admissionss.service.UserService;
import com.me.admissionss.util.FacultyRegistrationPDFExporter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/faculty")
@PreAuthorize("hasAnyAuthority('USER','ADMIN')")
@AllArgsConstructor
public class FacultyController {

    private final FacultyService facultyService;
    private final UserService userService;
    private final FacultyRegistrationService facultyRegistrationService;

    private static Integer pageNo = 0;
    private static Integer pageSize = 5;
    private static String sortBy = "name";
    private static String order = "asc";

    @GetMapping
    public String facultyList(
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String order,
            @NonNull Model model
    ) {
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("facultiesPage", facultyService.findAll(pageNo, pageSize, order, sortBy));

        FacultyController.savePaginationParams(pageNo, pageSize, sortBy, order);
        return "facultyList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/edit/{faculty}")
    public String facultyEditForm(@PathVariable Faculty faculty, @NonNull Model model) {
        model.addAttribute("faculty", faculty);

        return "facultyEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/delete/{faculty}")
    public String deleteFaculty(@PathVariable @NonNull Faculty faculty, Model model) {
        facultyService.deleteById(faculty.getId());

        return "redirect:/faculty?pageNo=" + pageNo + "&pageSize="
                + pageSize + "&sortBy=" + sortBy + "&order=" + order;
    }

    @Transactional
    @GetMapping("{faculty}")
    public String facultyPage(@PathVariable Faculty faculty,
                              @AuthenticationPrincipal User user,
                              Model model) {

        User userFromDb = userService.findByEmail(user.getEmail());

        model.addAttribute("faculty", faculty);
        model.addAttribute("alreadyParticipate",
                isUserAlreadyParticipate(userFromDb, faculty));
        model.addAttribute("facultySubjects",
                mapNotesIntersection(user.getNotes(), getFacultySubjectsMap(faculty)));
        model.addAttribute("usersTop",
                getTopUsersByNotes(facultyRegistrationService.findAllFacultyRegistrations(faculty)));
        return "facultyPage";
    }

    @PostMapping
    public String facultySave(
            @RequestParam String name,
            @RequestParam Integer contractPlaces,
            @RequestParam Integer budgetPlaces,
            @RequestParam("facultyId") @NonNull Faculty faculty
    ) {
        faculty.setName(name);
        faculty.setContractPlaces(contractPlaces);
        faculty.setBudgetPlaces(budgetPlaces);

        facultyService.save(faculty);
        return "redirect:/faculty";
    }

    @GetMapping("/add")
    public String addFaculty() {
        return "addFaculty";
    }

    @Transactional
    @PostMapping("/add")
    public String addFaculty( @RequestParam String name,
                              @RequestParam Integer contractPlaces,
                              @RequestParam Integer budgetPlaces,
                              @RequestParam Subject examSubject1,
                              @RequestParam Subject examSubject2,
                              @RequestParam Subject examSubject3,
                              Model model) {
        if (facultyService.isFacultyAlreadyExistsByName(name)) {
            model.addAttribute("message", "Faculty with such name already exists!");
            return "addFaculty";
        }

        Faculty faculty = Faculty.builder()
                .name(name)
                .budgetPlaces(budgetPlaces)
                .contractPlaces(contractPlaces)
                .finalized(false)
                .examSubjects(Set.of(examSubject1, examSubject2, examSubject3))
                .build();

        facultyService.save(faculty);
        return "redirect:/faculty";
    }

    @Transactional
    @PreAuthorize("hasAuthority('USER') && !hasAuthority('ADMIN')")
    @PostMapping("/{faculty}/participate")
    public String participateFaculty(@PathVariable Faculty faculty,
                                     @RequestParam Subject subject1, @RequestParam Double note1,
                                     @RequestParam Subject subject2, @RequestParam Double note2,
                                     @RequestParam Subject subject3, @RequestParam Double note3,
                                     @AuthenticationPrincipal User user) {

        Map<Subject, Double> notes = Map.of(
                subject1, note1,
                subject2, note2,
                subject3, note3);

        FacultyRegistration facultyRegistration = FacultyRegistration.builder()
                .faculty(faculty)
                .user(user)
                .subjects_notes(notes)
                .dateTime(LocalDateTime.now())
                .build();

        user.getNotes().putAll(notes);

        userService.saveUser(user);
        facultyRegistrationService.saveFacultyRegistration(facultyRegistration);

        return "redirect:/faculty/" + faculty.getId();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{faculty}/finalize")
    public String finalizeFaculty(@PathVariable Faculty faculty) {
        facultyService.finalizeFaculty(faculty);
        return "redirect:/faculty/" + faculty.getId();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{faculty}/export/pdf")
    public void exportToPDF(@PathVariable Faculty faculty, HttpServletResponse response) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=faculty_" + faculty.getName() + "_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<User> topUsersByNotes = List.copyOf(getTopUsersByNotes(facultyRegistrationService.findAllFacultyRegistrations(faculty)));

        FacultyRegistrationPDFExporter exporter = new FacultyRegistrationPDFExporter(topUsersByNotes, faculty);
        exporter.export(response);
    }

    private static void savePaginationParams(Integer pageNo, Integer pageSize, String sortBy, String order) {
        FacultyController.pageNo = pageNo;
        FacultyController.pageSize = pageSize;
        FacultyController.sortBy = sortBy;
        FacultyController.order = order;
    }

    private Boolean isUserAlreadyParticipate(User user, Faculty faculty) {
        return user.getSelectedFaculties()
                .stream()
                .map(FacultyRegistration::getFaculty)
                .map(Faculty::getName)
                .anyMatch(faculty.getName()::equals);
    }

    public static Set<User> getTopUsersByNotes(List<FacultyRegistration> facultyRegistrations) {
        return facultyRegistrations.stream()
                .sorted(Comparator
                        .comparingDouble(FacultyRegistration::getAverageExamNote)
                        .thenComparing(FacultyRegistration::getUserAverageSchoolNote)
                        .thenComparing(FacultyRegistration::getDateTime)
                        .reversed())
                .map(FacultyRegistration::getUser)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Map<Subject, Double> getFacultySubjectsMap(Faculty faculty) {
        Map<Subject, Double> facultySubjects = new HashMap<>();
        faculty.getExamSubjects().forEach(e -> facultySubjects.put(e, null));
        return facultySubjects;
    }

    public static Map<Subject, Double> mapNotesIntersection(Map<Subject, Double> map1,
                                                      Map<Subject, Double> map2) {
        Map<Subject, Double> result = new HashMap<>(map2);
        for (Map.Entry<Subject, Double> entry : map1.entrySet()) {
            if (map2.containsKey(entry.getKey())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }
}
