package com.example.aimers.Model;
public class AddMarkStudentModel{
    Student student;
    StudentResultModel result;
    public  AddMarkStudentModel(){}
    public AddMarkStudentModel(Student student, StudentResultModel result) {
        this.student = student;
        this.result = result;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public StudentResultModel getResult() {
        return result;
    }

    public void setResult(StudentResultModel result) {
        this.result = result;
    }
}
