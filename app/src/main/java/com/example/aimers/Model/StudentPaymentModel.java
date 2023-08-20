package com.example.aimers.Model;

public class StudentPaymentModel {
    private Student student;
    private Payment payment;

    public  StudentPaymentModel(){}
    public StudentPaymentModel(Student student, Payment payment) {
        this.student = student;
        this.payment = payment;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }
}
