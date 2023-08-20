package com.example.aimers.Model;

public class ResultShit {

    String resultShitId,name,batchId,status;

    public ResultShit(){}

    public ResultShit(String resultShitId, String name, String batchId, String status) {
        this.resultShitId = resultShitId;
        this.name = name;
        this.batchId = batchId;
        this.status = status;
    }

    public String getResultShitId() {
        return resultShitId;
    }

    public void setResultShitId(String resultShitId) {
        this.resultShitId = resultShitId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
