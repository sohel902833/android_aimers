package com.example.aimers.Model;

public class PaymentBatch {

  private String paymentBatchId;
  private Double monthlySalary;
  private String year;
  private String month;
  private String batchId;
  private Long totalCurrentStudent;
  public PaymentBatch(){}
    public PaymentBatch(String paymentBatchId, Double monthlySalary, String year, String month, String batchId,Long totalCurrentStudent) {
        this.paymentBatchId = paymentBatchId;
        this.monthlySalary = monthlySalary;
        this.year = year;
        this.month = month;
        this.batchId = batchId;
        this.totalCurrentStudent=totalCurrentStudent;
    }

    public String getPaymentBatchId() {
        return paymentBatchId;
    }

    public void setPaymentBatchId(String paymentBatchId) {
        this.paymentBatchId = paymentBatchId;
    }

    public Double getMonthlySalary() {
        return monthlySalary;
    }

    public void setMonthlySalary(Double monthlySalary) {
        this.monthlySalary = monthlySalary;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public Long getTotalCurrentStudent() {
        return totalCurrentStudent;
    }

    public void setTotalCurrentStudent(Long totalCurrentStudent) {
        this.totalCurrentStudent = totalCurrentStudent;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
}

