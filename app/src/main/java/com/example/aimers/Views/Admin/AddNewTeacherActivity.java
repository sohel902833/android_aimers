package com.example.aimers.Views.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aimers.Model.Teacher;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.example.aimers.api.ApiRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddNewTeacherActivity extends AppCompatActivity {
    public static final String ACTION_EDIT="edit";

    private Toolbar toolbar;
    private AppBar appBar;

    private EditText teacherNameEt,teacherPhoneEt,teacherIdEt,teacherPasswordEt,emailEt,forHomePageEt,titleEt;
    private Button createTeacherButton;
    private TextView titleTv,teacherIdTitle,teacherPhoneTitle,forHomePageEtTitle;

    private ProgressDialog progressDialog;
    String email="";

     String action="",comeFrom="admin";
     String editUserPhone="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_teacher);

        action=getIntent().getStringExtra("action");
        editUserPhone=getIntent().getStringExtra("phone");
        comeFrom=getIntent().getStringExtra("comeFrom");
        init();
        if(action.equals(ACTION_EDIT)){
            if(comeFrom.equals("teacherProfile")){
                titleTv.setText("Update Your Profile");
                createTeacherButton.setText("Update");
                teacherPhoneEt.setVisibility(View.GONE);
                teacherIdEt.setVisibility(View.GONE);
                teacherIdTitle.setVisibility(View.GONE);
                teacherPhoneTitle.setVisibility(View.GONE);
                forHomePageEt.setVisibility(View.GONE);
                forHomePageEtTitle.setVisibility(View.GONE);
            }else{
                titleTv.setText("Edit Teacher");
                createTeacherButton.setText("Update Teacher");
                teacherPhoneEt.setVisibility(View.GONE);
                teacherIdEt.setVisibility(View.GONE);
                teacherIdTitle.setVisibility(View.GONE);
                teacherPhoneTitle.setVisibility(View.GONE);
            }

        }

        teacherIdEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String text=charSequence.toString();
                    teacherPasswordEt.setText(""+text);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        createTeacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String teacherName=teacherNameEt.getText().toString();
                String teacherId=teacherIdEt.getText().toString();
                String teacherPhone=teacherPhoneEt.getText().toString();
                String password=teacherPasswordEt.getText().toString().trim();
                 email=emailEt.getText().toString().trim();
                String forHomePage=forHomePageEt.getText().toString();
                String title=titleEt.getText().toString();
                if(teacherName.isEmpty()){
                    teacherNameEt.setError("Enter Teacher Name.");
                    teacherNameEt.requestFocus();
                }else if(teacherId.isEmpty()){
                    teacherIdEt.setError("Enter Teacher Id");
                    teacherIdEt.requestFocus();
                }else if(teacherPhone.isEmpty()){
                    teacherPhoneEt.setError("Enter Teacher Phone Number.");
                    teacherPhoneEt.requestFocus();
                }else if(password.isEmpty()){
                    teacherPasswordEt.setError("Enter Password.");
                    teacherPasswordEt.requestFocus();
                }else if(forHomePage.isEmpty()){
                    forHomePageEt.setError("Enter This Value.");
                    forHomePageEt.requestFocus();
                }else if(title.isEmpty()){
                    titleEt.setError("Enter Teacher Title.");
                    titleEt.requestFocus();
                }else{
                    if(action.equals(ACTION_EDIT)){
                        updateTeacher(teacherName,teacherId,teacherPhone.trim(),password,forHomePage,title);
                    }else{
                        createTeacher(teacherName,teacherId,teacherPhone.trim(),password,forHomePage,title);
                    }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(action.equals(ACTION_EDIT)) {
            progressDialog.setMessage("Loading..");
            progressDialog.show();
            ApiRef.teacherRef.child(editUserPhone).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressDialog.dismiss();
                    if (dataSnapshot.exists()) {
                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                        teacherNameEt.setText("" + teacher.getName());
                        teacherPhoneEt.setText("" + teacher.getPhone());
                        emailEt.setText("" + teacher.getEmail());
                        teacherPasswordEt.setText("" + teacher.getPassword());
                        teacherIdEt.setText("" + teacher.getCode());
                        forHomePageEt.setText("" + teacher.getForHomePage());
                        titleEt.setText("" + teacher.getTitle());
                    } else {
                        Toast.makeText(AddNewTeacherActivity.this, "No Teacher Found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(AddNewTeacherActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void init(){
        //setup appbar
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        if(action.equals(ACTION_EDIT)){
            if(comeFrom.equals("teacherProfile")){
                appBar.init(toolbar,"Update Profile");
            }else{
                appBar.init(toolbar,"Update Teacher");
            }
        }else{
            appBar.init(toolbar,"Add New Teacher");
        }

        appBar.hideBackButton();
        //end setup appbar;

        teacherIdEt=findViewById(R.id.ant_teacherId);
        teacherIdTitle=findViewById(R.id.teacherIdTitle);
        teacherPhoneTitle=findViewById(R.id.teacherPhoneTitle);
        teacherNameEt=findViewById(R.id.ant_teacherNameEt);
        teacherPhoneEt=findViewById(R.id.ant_teacherPhoneEt);
        teacherPasswordEt=findViewById(R.id.ant_PasswordEt);
        forHomePageEt=findViewById(R.id.ant_forHomePageEt);
        titleEt=findViewById(R.id.ant_titleEt);
        createTeacherButton=findViewById(R.id.ant_AddTeacherButtonId);
        emailEt=findViewById(R.id.ant_teacherEmailEt);
        titleTv=findViewById(R.id.ant_TitleTv);
        forHomePageEtTitle=findViewById(R.id.forHomePageEtTitle);

        progressDialog=new ProgressDialog(this);



    }
    private void createTeacher(String teacherName, String code, String phone, String password,String forHomePage,String title) {
        progressDialog.setMessage("Creating New Teacher..");
        progressDialog.setTitle("Please Wait..");
        progressDialog.show();

        //check already teacher exists or not with same phone number and id
        Query query = ApiRef.teacherRef
                .orderByChild("phone")
                .equalTo(phone);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    progressDialog.dismiss();
                    Toast.makeText(AddNewTeacherActivity.this, "Teacher already Exists With This Phone", Toast.LENGTH_SHORT).show();
                }else{
                    //no teacher exists using this phone or id
                    String teacherId=ApiRef.teacherRef.push().getKey();
                    Teacher teacher=new Teacher(teacherId,teacherName,email,phone,password,code,forHomePage,title);
                    ApiRef.teacherRef.child(phone)
                            .setValue(teacher).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if(task.isSuccessful()){
                                        Toast.makeText(AddNewTeacherActivity.this, "Teacher Created", Toast.LENGTH_SHORT).show();
                                        teacherNameEt.setText("");
                                        emailEt.setText("");
                                        teacherPhoneEt.setText("");
                                        teacherIdEt.setText("");
                                        teacherPasswordEt.setText("");
                                    }else{
                                        Toast.makeText(AddNewTeacherActivity.this, "Teacher Create Failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(AddNewTeacherActivity.this, "Teacher Create Failed.", Toast.LENGTH_SHORT).show();
            }
        });





    }
    private void updateTeacher(String teacherName, String code, String phone, String password,String forHomePage,String title) {
        progressDialog.setMessage("Updating Teacher..");
        progressDialog.setTitle("Please Wait..");
        progressDialog.show();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("name",teacherName);
        hashMap.put("email",email);
        hashMap.put("password",password);
        hashMap.put("title",title);
        hashMap.put("forHomePage",forHomePage);
        ApiRef.teacherRef.child(phone)
                .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(AddNewTeacherActivity.this, "Teacher Updated.", Toast.LENGTH_SHORT).show();
                            teacherNameEt.setText("");
                            emailEt.setText("");
                            teacherPhoneEt.setText("");
                            teacherIdEt.setText("");
                            teacherPasswordEt.setText("");
                            finish();
                        }else{
                            Toast.makeText(AddNewTeacherActivity.this, "Teacher Update Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}