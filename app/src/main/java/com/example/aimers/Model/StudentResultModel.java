package com.example.aimers.Model;

public class StudentResultModel {

    String resultId,studentId,batchId,shitId,position,gpa,comment,totalMark;

    public  StudentResultModel(){

    }

    public StudentResultModel(String resultId, String studentId, String batchId, String shitId, String position, String gpa, String comment, String totalMark) {
        this.resultId = resultId;
        this.studentId = studentId;
        this.batchId = batchId;
        this.shitId = shitId;
        this.position = position;
        this.gpa = gpa;
        this.comment = comment;
        this.totalMark = totalMark;
    }

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getShitId() {
        return shitId;
    }

    public void setShitId(String shitId) {
        this.shitId = shitId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getGpa() {
        return gpa;
    }

    public void setGpa(String gpa) {
        this.gpa = gpa;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTotalMark() {
        return totalMark;
    }

    public void setTotalMark(String totalMark) {
        this.totalMark = totalMark;
    }
}
