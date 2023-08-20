package com.example.aimers.Model;

public class User {
    public static  final String ADMIN="admin";
    public static  final String STUDENT="student";
    public static  final String TEACHER="teacher";
    String phone;
    String password;
    String userType;
    public User(){}
    public User(String phone, String password, String userType) {
        this.phone = phone;
        this.password = password;
        this.userType = userType;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
