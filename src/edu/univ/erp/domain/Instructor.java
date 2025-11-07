package edu.univ.erp.domain;

public class Instructor {
    private int userId; // This links to User.userId
    private String department;

    public Instructor(int userId, String department) {
        this.userId = userId;
        this.department = department;
    }

    // --- Getters ---
    public int getUserId() { return userId; }
    public String getDepartment() { return department; }
}