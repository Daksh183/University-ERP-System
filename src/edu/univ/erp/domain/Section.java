package edu.univ.erp.domain;

public class Section {
    private int sectionId;
    private int courseId; // Links to Course.courseId
    private int instructorId; // Links to Instructor.userId
    private String dayTime; // e.g., "MWF 10:00-10:50"
    private String room;
    private int capacity;
    private String semester;
    private int year;

    public Section(int sectionId, int courseId, int instructorId, String dayTime, String room, int capacity, String semester, int year) {
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.dayTime = dayTime;
        this.room = room;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
    }

    // --- Getters ---
    // (You can add all the getters here)
    public int getSectionId() { return sectionId; }
    public int getCourseId() { return courseId; }
    public int getCapacity() { return capacity; }
    public int getInstructorId() { return instructorId; }
}