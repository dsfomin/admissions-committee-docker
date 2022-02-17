package com.me.admissionss.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.me.admissionss.controller.FacultyController;
import com.me.admissionss.entity.Faculty;
import com.me.admissionss.entity.Subject;
import com.me.admissionss.entity.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public class FacultyRegistrationPDFExporter {
    private final List<User> listUsers;
    private final Faculty faculty;

    public FacultyRegistrationPDFExporter(List<User> listUsers, Faculty faculty) {
        this.listUsers = listUsers;
        this.faculty = faculty;
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BaseColor.BLUE);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(BaseColor.WHITE);

        cell.setPhrase(new Phrase("User ID", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("E-mail", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Avrg school note", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Exam Notes", font));
        table.addCell(cell);
    }

    private void writeTableData(PdfPTable table) {
        PdfPCell cell = new PdfPCell();

        for (User user : listUsers) {
            String userExamSubjects = findUserExamSubjects(user, faculty);
            cell.setBackgroundColor(finalizeUser(user));

            cell.setPhrase(new Phrase(String.valueOf(user.getId())));
            table.addCell(cell);

            cell.setPhrase(new Phrase(user.getEmail()));
            table.addCell(cell);

            cell.setPhrase(new Phrase(String.valueOf(user.getAverageSchoolNote())));
            table.addCell(cell);

            cell.setPhrase(new Phrase(userExamSubjects));
            table.addCell(cell);

        }
    }

    private String findUserExamSubjects(User user, Faculty faculty) {
        Map<Subject, Double> userFacultySubjects = FacultyController.mapNotesIntersection(user.getNotes(), FacultyController.getFacultySubjectsMap(faculty));
        StringBuilder stringBuilder = new StringBuilder();
        userFacultySubjects.forEach((v, k) -> stringBuilder.append(v).append(" - ").append(k).append("\n"));
        return stringBuilder.toString();
    }

    private BaseColor finalizeUser(User user) {
        int userIndex = listUsers.indexOf(user);
        if (faculty.getBudgetPlaces() > userIndex) {
            return new BaseColor(142, 240, 145);
        } else if (faculty.getBudgetPlaces() + faculty.getContractPlaces() > userIndex
                && faculty.getBudgetPlaces() <= userIndex) {
            return new BaseColor(129, 183, 244);
        }
        return BaseColor.LIGHT_GRAY;
    }

    public void export(HttpServletResponse response) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(BaseColor.BLUE);

        Paragraph p = new Paragraph("List of Participants", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {0.5f, 2f, 1.5f, 2.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table);

        document.add(table);

        document.close();

    }
}
