package com.example.aimers.Model;

public class Batch {
    String batchId,batchName="",classId,group,session;

    Batch(){}

    public Batch(String batchId, String batchName, String classId, String group,  String session) {
        this.batchId = batchId;
        this.batchName = batchName;
        this.classId = classId;
        this.group = group;
        this.session = session;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }
}
