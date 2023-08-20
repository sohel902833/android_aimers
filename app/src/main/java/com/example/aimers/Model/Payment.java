package com.example.aimers.Model;

public class Payment {
    String paymentId;
    String studentId;
    String paymentBatchId;
    String studentBatchId;

    Double amount;
    String date;
    public Payment(){}
    public Payment(String paymentId, String studentId, String paymentBatchId, String studentBatchId, Double amount, String date) {
        this.paymentId = paymentId;
        this.studentId = studentId;
        this.paymentBatchId = paymentBatchId;
        this.studentBatchId = studentBatchId;
        this.amount = amount;
        this.date = date;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getPaymentBatchId() {
        return paymentBatchId;
    }

    public void setPaymentBatchId(String paymentBatchId) {
        this.paymentBatchId = paymentBatchId;
    }

    public String getStudentBatchId() {
        return studentBatchId;
    }

    public void setStudentBatchId(String studentBatchId) {
        this.studentBatchId = studentBatchId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
