package com.example.aimers.Model;

public class NewClassModel {
    String departmentId,departmentName;
    String batchId,batchName="",group,session;

    public  NewClassModel(){}
    public NewClassModel(String departmentId, String departmentName, String batchId, String batchName, String group, String session) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.batchId = batchId;
        this.batchName = batchName;
        this.group = group;
        this.session = session;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
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
