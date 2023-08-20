package com.example.aimers.Model;

public class TeacherClass {
    String classId,teacherPhone,batchId,subjectCode,subjectName;

    public TeacherClass(){}
    public TeacherClass(String classId, String teacherPhone, String batchId, String subjectCode, String subjectName) {
        this.classId = classId;
        this.teacherPhone = teacherPhone;
        this.batchId = batchId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getTeacherPhone() {
        return teacherPhone;
    }

    public void setTeacherPhone(String teacherPhone) {
        this.teacherPhone = teacherPhone;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
