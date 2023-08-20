package com.example.aimers.Views.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aimers.Adapter.SingleStudentAttendanceAdapter;
import com.example.aimers.LocalDb.UserDb;
import com.example.aimers.Model.Attendance;
import com.example.aimers.Model.Student;
import com.example.aimers.Model.Teacher;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.api.ApiRef;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentAttendanceProfile extends AppCompatActivity {

    private AppBar appBar;
    private Toolbar toolbar;

    private TextView teacherNameTv,teacherPhoneTv,subjectDetailsTv,attendanceDetailsTv;
    private RecyclerView attendanceRecyclerView;

    private CustomDialog customDialog;
    private ProgressDialog progressDialog;
    private UserDb userDb;


    private List<Attendance> attendanceList=new ArrayList<>();
    private SingleStudentAttendanceAdapter attendanceAdapter;

    String classId="",subjectName="",subjectCode="",teacherPhone="";
    int totalPresent=0;
    int totalAbsent=0;
    private Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance_profile);

        classId=getIntent().getStringExtra("classId");
        subjectName=getIntent().getStringExtra("subjectName");
        subjectCode=getIntent().getStringExtra("subjectCode");
        teacherPhone=getIntent().getStringExtra("teacherPhone");
      init();


      student=userDb.getStudentData();
        attendanceAdapter=new SingleStudentAttendanceAdapter(this,attendanceList,student.getRoll(),false);
        attendanceRecyclerView.setAdapter(attendanceAdapter);



    }
    private  void init(){
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,""+subjectName+"("+subjectCode+")");

        teacherNameTv=findViewById(R.id.sap_teacherNameTv);
        teacherPhoneTv=findViewById(R.id.sap_teacherPhoneTv);
        subjectDetailsTv=findViewById(R.id.sap_subjectDetailsTv);
        attendanceDetailsTv=findViewById(R.id.spd_attendanceDetailsTv);
        attendanceRecyclerView=findViewById(R.id.spd_singleStudentAttendanceRecyclerViewId);
        attendanceRecyclerView.setHasFixedSize(true);
        attendanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        progressDialog=new ProgressDialog(this);
        customDialog=new CustomDialog(this);
        userDb=new UserDb(this);



    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiRef.teacherRef.child(teacherPhone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Teacher teacher=dataSnapshot.getValue(Teacher.class);
                            teacherNameTv.setText("Teacher Name: "+teacher.getName());
                            teacherPhoneTv.setText(""+teacher.getPhone());
                            subjectDetailsTv.setText("Subject Name: "+subjectName+"\n"+"Subject Code: "+subjectCode);


                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(StudentAttendanceProfile.this, "Teacher Not Found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });

        ///get student attendance info
        ApiRef.attendanceRef.child(student.getRoll())
                .child(classId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        progressDialog.dismiss();
                        if(dataSnapshot.exists()){
                            totalPresent=0;
                            totalAbsent=0;
                            attendanceList.clear();
                            for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                Attendance attendance=dataSnapshot1.getValue(Attendance.class);
                                if(attendance.getAttendance().equals(Attendance.PRESENT)){
                                    totalPresent+=1;
                                }else if(attendance.getAttendance().equals(Attendance.ABSENT)){
                                    totalAbsent+=1;
                                }
                                attendanceList.add(attendance);
                                attendanceAdapter.notifyDataSetChanged();
                            }

                            attendanceDetailsTv.setText("Total Class: "+attendanceList.size()+",  Ab:"+totalAbsent+"   Pr: "+totalPresent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });


    }
}