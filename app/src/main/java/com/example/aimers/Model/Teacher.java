package com.example.aimers.Model;

public class Teacher {
    String teacherId,name,email,phone="",password,code,forHomePage="",title="";
    public Teacher(){}

    public Teacher(String teacherId, String name, String email, String phone, String password, String code,String forHomePage,String title) {
        this.teacherId = teacherId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.code = code;
        this.forHomePage=forHomePage;
        this.title=title;
    }


    public String getForHomePage() {
        return forHomePage;
    }

    public void setForHomePage(String forHomePage) {
        this.forHomePage = forHomePage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
