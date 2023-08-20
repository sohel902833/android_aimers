package com.example.aimers.Views.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.aimers.Adapter.StudentAttendanceAdapter;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentAttendanceActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private AppBar appBar;
    private RecyclerView recyclerView;
    private StudentAttendanceAdapter studentAttendanceAdapter;
    private List<Student> studentList=new ArrayList<>();

    private ProgressDialog progressDialog;
    private CustomDialog customDialog;





    String batchId="",classId="",subjectName="",subjectCode="";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);
        batchId=getIntent().getStringExtra("batchId");
        classId=getIntent().getStringExtra("classId");
        subjectName=getIntent().getStringExtra("subjectName");
        subjectCode=getIntent().getStringExtra("subjectCode");
        init();

        studentAttendanceAdapter=new StudentAttendanceAdapter(this,studentList,classId);
        recyclerView.setAdapter(studentAttendanceAdapter);

        studentAttendanceAdapter.setOnItemClickListner(new StudentAttendanceAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position,Student student) {
                Intent intent=new Intent(StudentAttendanceActivity.this,StudentPresentDetailsActivity.class);
                intent.putExtra("roll",student.getRoll());
                intent.putExtra("studentId",student.getStudentId());
                intent.putExtra("classId",classId);
                startActivity(intent);
            }

            @Override
            public void onAbsent(int position, Student student) {
                createAttendance(Attendance.ABSENT,student);
            }

            @Override
            public void onPresent(int position, Student student) {
                createAttendance(Attendance.PRESENT,student);
            }
        });



    }



    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("");
        progressDialog.setCancelable(false);
        progressDialog.show();


        Query query = ApiRef.studentRef
                .orderByChild("batchId")
                .equalTo(batchId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if(snapshot.exists()){
                    studentList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        Student student=snapshot1.getValue(Student.class);
                        studentList.add(student);
                        studentAttendanceAdapter.notifyDataSetChanged();
                    }
                }else{
                    Toast.makeText(StudentAttendanceActivity.this, "No Student Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(StudentAttendanceActivity.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void init(){
        //setup appbar
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,""+subjectName+"("+subjectCode+")");
        //end setup appbar;

        recyclerView=findViewById(R.id.studentListRecyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog=new ProgressDialog(this);
        customDialog=new CustomDialog(this);

    }

    private  void createAttendance(String attendance,Student student){
        progressDialog.setMessage("Making As "+attendance);
        progressDialog.show();

        String attendanceDate=Attendance.getTodayDate();
        String time= Attendance.getCurrentTime();
        HashMap<String,Object> attendanceMap=new HashMap<>();
        attendanceMap.put("time",time);
        attendanceMap.put("date",attendanceDate);
        attendanceMap.put("attendance",attendance);
        attendanceMap.put("classId",classId);

        ApiRef.attendanceRef
                .child(classId)
                .child(student.getRoll())
                .child(attendanceDate)
                .updateChildren(attendanceMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(StudentAttendanceActivity.this, student.getRoll()+" is "+attendance, Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(StudentAttendanceActivity.this, "Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });




    }



}