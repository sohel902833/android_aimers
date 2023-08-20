package com.example.aimers.LocalDb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.aimers.Model.Student;
import com.example.aimers.Model.Teacher;
import com.example.aimers.Model.User;
import com.example.aimers.Views.Common.LoginActivity;
import com.google.gson.Gson;


public class UserDb {
    private Activity activity;
    public UserDb(Activity activity) {
        this.activity = activity;
    }
    public void setUserData(User user) {
        SharedPreferences sharedPreferences=activity.getSharedPreferences("userDb", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("user", json);
        editor.commit();
    }
    public void setTeacherData(Teacher teacher) {
        SharedPreferences sharedPreferences=activity.getSharedPreferences("teacherDb", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(teacher);
        editor.putString("teacher", json);
        editor.commit();
    }
    public void setStudentData(Student student) {
        SharedPreferences sharedPreferences=activity.getSharedPreferences("studentDb", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(student);
        editor.putString("student", json);
        editor.commit();
    }
    public Student getStudentData(){
        Student student =null;
        SharedPreferences sharedPreferences=activity.getSharedPreferences("studentDb", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("student","");
        student = gson.fromJson(json, Student.class);
        return  student;
    }  public Teacher getTeacherData(){
        Teacher teacher =null;
        SharedPreferences sharedPreferences=activity.getSharedPreferences("teacherDb", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("teacher","");
        teacher = gson.fromJson(json, Teacher.class);
        return  teacher;
    }
    public User getUserData(){
        User user=null;
        SharedPreferences sharedPreferences=activity.getSharedPreferences("userDb", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("user","");
        user = gson.fromJson(json, User.class);
        return  user;
    }
    public void removeUserData(){
        SharedPreferences userShared = activity.getSharedPreferences("userDb", Context.MODE_PRIVATE);
        userShared.edit().clear().apply();
    }
    public void removeTeacherData(){
        SharedPreferences userShared = activity.getSharedPreferences("teacherDb", Context.MODE_PRIVATE);
        userShared.edit().clear().apply();
    }
  public void removeStudentData(){
        SharedPreferences userShared = activity.getSharedPreferences("studentDb", Context.MODE_PRIVATE);
        userShared.edit().clear().apply();
    }

    public void logoutUser(Activity activity){
        removeUserData();
        removeTeacherData();
        removeStudentData();
        Intent intent=new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

}
