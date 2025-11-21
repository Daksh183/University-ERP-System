package edu.univ.erp.service;

import edu.univ.erp.auth.AuthDAO;
import edu.univ.erp.auth.AuthDAOImpl;
import edu.univ.erp.auth.PasswordHasher;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.ServiceException;

import java.sql.SQLException;
import java.util.List;

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

    public void createNewUser(String username, String password, String role, String rollOrDept, String program) throws SQLException, ServiceException {
        String passwordHash = PasswordHasher.hashPassword(password);
        int newUserId = authDAO.createUser(username, role, passwordHash);

        if (newUserId == -1) {
            throw new ServiceException("Could not create user in Auth DB.");
        }

        if ("Student".equals(role)) {
            studentDAO.createStudentProfile(newUserId, rollOrDept, program);
        } else if ("Instructor".equals(role)) {
            instructorDAO.createInstructorProfile(newUserId, rollOrDept);
        }
    }

    public void updateUser(int userId, String username, String role, String rollOrDept, String program) throws SQLException, ServiceException {
        authDAO.updateUsername(userId, username);
        if ("Student".equals(role)) {
            studentDAO.updateStudentProfile(userId, rollOrDept, program);
        } else if ("Instructor".equals(role)) {
            instructorDAO.updateInstructorProfile(userId, rollOrDept);
        }
    }

    public void resetUserPassword(String username, String newPlainPassword) throws SQLException, ServiceException {
        int userId = authDAO.getUserIdByUsername(username);
        if (userId == -1) throw new ServiceException("User '" + username + "' not found.");
        String newHash = PasswordHasher.hashPassword(newPlainPassword);
        authDAO.updatePassword(userId, newHash);
    }

    // REMOVED: unlockUser method

    public List<Student> getAllStudents() throws SQLException { return studentDAO.getAllStudents(); }
    public List<Instructor> getAllInstructors() throws SQLException { return instructorDAO.getAllInstructors(); }

    public void createCourse(String code, String title, int credits) throws SQLException {
        courseDAO.createCourse(code, title, credits);
    }

    public void updateCourse(String code, String title, int credits) throws SQLException {
        courseDAO.updateCourse(code, title, credits);
    }

    public void createSection(int courseId, String instructorUsername, String dayTime, String room, int capacity) throws SQLException, ServiceException {
        int instructorId = authDAO.getUserIdByUsername(instructorUsername);
        if (instructorId == -1) throw new ServiceException("Instructor '" + instructorUsername + "' not found.");
        courseDAO.createSection(courseId, instructorId, dayTime, room, capacity, "Monsoon", 2025);
    }

    public void updateSection(int sectionId, int courseId, String instructorUsername, String dayTime, String room, int capacity, String semester, int year) throws SQLException, ServiceException {
        int instructorId = authDAO.getUserIdByUsername(instructorUsername);
        if (instructorId == -1) throw new ServiceException("Instructor '" + instructorUsername + "' not found.");
        courseDAO.updateSection(sectionId, courseId, instructorId, dayTime, room, capacity, semester, year);
    }
}