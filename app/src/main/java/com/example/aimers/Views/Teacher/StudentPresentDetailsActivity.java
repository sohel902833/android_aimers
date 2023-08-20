package com.example.aimers.Views.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aimers.Adapter.SingleStudentAttendanceAdapter;
import com.example.aimers.Interfaces.CustomDialogClickListner;
import com.example.aimers.LocalDb.UserDb;
import com.example.aimers.Model.Attendance;
import com.example.aimers.Model.Student;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.api.ApiRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentPresentDetailsActivity extends AppCompatActivity {

    private AppBar appBar;
    private Toolbar toolbar;

    private TextView nameTv,rollTv,studentDetailsTv,attendanceDetailsTv;
    private RecyclerView attendanceRecyclerView;

    private CustomDialog customDialog;
    private ProgressDialog progressDialog;
    private UserDb userDb;


    private List<Attendance> attendanceList=new ArrayList<>();
    private SingleStudentAttendanceAdapter attendanceAdapter;

    String classId="",studentRoll="",studentId="";
    int totalPresent=0;
    int totalAbsent=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_present_details);

        classId=getIntent().getStringExtra("classId");
        studentRoll=getIntent().getStringExtra("roll");
        studentId=getIntent().getStringExtra("studentId");
        init();


        attendanceAdapter=new SingleStudentAttendanceAdapter(this,attendanceList,studentRoll);
        attendanceRecyclerView.setAdapter(attendanceAdapter);


        attendanceAdapter.setOnItemClickListner(new SingleStudentAttendanceAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onAbsent(int position, Attendance attendance,TextView statusTv) {
                customDialog.show("Are you sure you want to change this attendance? ");
                customDialog.onActionClick(new CustomDialogClickListner() {
                    @Override
                    public void onPositiveButtonClicked(View view, AlertDialog dialog) {
                        updateAttendance(Attendance.ABSENT,attendance,statusTv);
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegativeButtonClicked(View view, AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });

            }

            @Override
            public void onPresent(int position, Attendance attendance,TextView statusTv) {
                customDialog.show("Are you sure you want to change this attendance? ");
                customDialog.onActionClick(new CustomDialogClickListner() {
                    @Override
                    public void onPositiveButtonClicked(View view, AlertDialog dialog) {
                        updateAttendance(Attendance.PRESENT,attendance,statusTv);
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegativeButtonClicked(View view, AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });

            }
        });


    }
    private  void init(){
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,"Student Info ("+studentRoll+")");

        nameTv=findViewById(R.id.spd_studentNameTv);
        rollTv=findViewById(R.id.spd_studentRollTv);
        studentDetailsTv=findViewById(R.id.spd_studentDetailsTv);
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

        ApiRef.studentRef.child(studentId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Student student=dataSnapshot.getValue(Student.class);
                            nameTv.setText(""+student.getName());
                            rollTv.setText(""+student.getRoll());

                            String email=student.getEmail().isEmpty()?"":"Email: "+student.getEmail()+"\n";
                            String phone=student.getPhone().isEmpty()?"":"Phone: "+student.getPhone()+"\n";

                            studentDetailsTv.setText(phone+email);


                        }else{
                            finish();
                            progressDialog.dismiss();
                            Toast.makeText(StudentPresentDetailsActivity.this, "Student Not Found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                    }
                });

        ///get student attendance info
        ApiRef.attendanceRef
                .child(classId).child(studentRoll)
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

    private  void updateAttendance(String pr,Attendance at,TextView statusTv){
        progressDialog.setMessage("Making As "+pr);
        progressDialog.show();

        HashMap<String,Object> attendanceMap=new HashMap<>();
        attendanceMap.put("attendance",pr);
        attendanceMap.put("time",Attendance.getCurrentTime());

        ApiRef.attendanceRef.child(at.getClassId()).child(studentRoll)
                .child(at.getDate())
                .updateChildren(attendanceMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            statusTv.setText(""+pr);
                            if(pr.equals(Attendance.ABSENT)){
                                statusTv.setTextColor(Color.RED);
                            }else{
                                statusTv.setTextColor(getResources().getColor(R.color.greenDark));
                            }
                            onStart();

                            progressDialog.dismiss();
                            Toast.makeText(StudentPresentDetailsActivity.this, studentRoll+" Updated As "+pr, Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(StudentPresentDetailsActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}