package com.example.aimers.Model;

public class StudentSmsModel {
    Student student;
    String message;

    StudentSmsModel(){}

    public StudentSmsModel(Student student, String message) {
        this.student = student;
        this.message = message;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
