package edu.univ.erp.service;

import edu.univ.erp.auth.AuthDAO;
import edu.univ.erp.auth.AuthDAOImpl;
import edu.univ.erp.auth.PasswordHasher;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
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
        this.instructorDAO = new InstructorDAOImpl(); // We need to create this
        this.courseDAO = new CourseDAOImpl();
    }

    /**
     * Creates a new user in the system (Auth DB + ERP DB).
     * @param username The new user's username
     * @param password The new user's plaintext password
     * @param role "Student" or "Instructor"
     * @param rollOrDept The student's roll number OR the instructor's department
     * @throws SQLException
     * @throws ServiceException
     */
    public void createNewUser(String username, String password, String role, String rollOrDept) throws SQLException, ServiceException {
        // 1. Hash the password
        String passwordHash = PasswordHasher.hashPassword(password);

        // 2. Create the user in the Auth DB
        // We need a new DAO method that returns the generated user_id
        int newUserId = authDAO.createUser(username, role, passwordHash);

        if (newUserId == -1) {
            throw new ServiceException("Could not create user in Auth DB.");        }

        // 3. Create the profile in the ERP DB
        if ("Student".equals(role)) {
            studentDAO.createStudentProfile(newUserId, rollOrDept);
        } else if ("Instructor".equals(role)) {
            instructorDAO.createInstructorProfile(newUserId, rollOrDept);
        }
    }

    /**
     * Creates a new course in the catalog.
     */
    public void createCourse(String code, String title, int credits) throws SQLException {
        courseDAO.createCourse(code, title, credits);
    }

    /**
     * Creates a new section for a course.
     */
    public void createSection(int courseId, int instructorId, String dayTime, String room, int capacity) throws SQLException {
        // In a real app, semester and year would be inputs
        courseDAO.createSection(courseId, instructorId, dayTime, room, capacity, "Monsoon", 2025);
    }
}