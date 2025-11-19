package edu.univ.erp.service;

import edu.univ.erp.auth.AuthDAO;
import edu.univ.erp.auth.AuthDAOImpl;
import edu.univ.erp.auth.PasswordHasher;
import edu.univ.erp.data.*;
import edu.univ.erp.service.ServiceException;

import java.sql.SQLException;

public class AdminService {

    private AuthDAO authDAO;
    private StudentDAO studentDAO;
    private InstructorDAO instructorDAO;
    private CourseDAO courseDAO;

    public AdminService() {
        this.authDAO = new AuthDAOImpl();
        this.studentDAO = new StudentDAOImpl();
        this.instructorDAO = new InstructorDAOImpl();
        this.courseDAO = new CourseDAOImpl();
    }

    public void createNewUser(String username, String password, String role, String rollOrDept) throws SQLException, ServiceException {
        String passwordHash = PasswordHasher.hashPassword(password);
        int newUserId = authDAO.createUser(username, role, passwordHash);

        if (newUserId == -1) {
            throw new ServiceException("Could not create user in Auth DB.");
        }

        if ("Student".equals(role)) {
            studentDAO.createStudentProfile(newUserId, rollOrDept);
        } else if ("Instructor".equals(role)) {
            instructorDAO.createInstructorProfile(newUserId, rollOrDept);
        }
    }

    public void createCourse(String code, String title, int credits) throws SQLException {
        courseDAO.createCourse(code, title, credits);
    }

    public void updateCourse(String code, String title, int credits) throws SQLException {
        courseDAO.updateCourse(code, title, credits);
    }

    public void createSection(int courseId, int instructorId, String dayTime, String room, int capacity) throws SQLException {
        courseDAO.createSection(courseId, instructorId, dayTime, room, capacity, "Monsoon", 2025);
    }

    // --- UPDATED METHOD ---
    public void updateSection(int sectionId, int courseId, int instructorId, String dayTime, String room, int capacity, String semester, int year) throws SQLException {
        courseDAO.updateSection(sectionId, courseId, instructorId, dayTime, room, capacity, semester, year);
    }
}