package com.example.aimers.Views.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aimers.Adapter.StudentListAdapter;
import com.example.aimers.Interfaces.CustomDialogClickListner;
import com.example.aimers.Model.Student;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.api.ApiRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentListActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AppBar appBar;
    private RecyclerView recyclerView;
    private FloatingActionButton addNewStudentButton;

    private StudentListAdapter studentListAdapter;
    private List<Student> studentList=new ArrayList<>();
    private String batchId="";

    String phone="",email="";
    private ProgressDialog progressDialog;
    private CustomDialog customDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);
        batchId=getIntent().getStringExtra("batchId");
        init();


        studentListAdapter=new StudentListAdapter(this,studentList);
        recyclerView.setAdapter(studentListAdapter);

        addNewStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNewStudentDialog();
            }
        });

        studentListAdapter.setOnItemClickListner(new StudentListAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onEdit(int position, Student student) {
                    showUpdateStudentDialog(student);
            }

            @Override
            public void onDelete(int position, Student student) {
                customDialog.show("Are You Sure? You Want to delete this student?");
                customDialog.onActionClick(new CustomDialogClickListner() {
                    @Override
                    public void onPositiveButtonClicked(View view, AlertDialog dialog) {
                         deleteStudent(student,dialog);
                    }

                    @Override
                    public void onNegativeButtonClicked(View view, AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("");
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
                        studentListAdapter.notifyDataSetChanged();
                    }
                }else{
                    Toast.makeText(StudentListActivity.this, "No Student Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(StudentListActivity.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(){
        //setup appbar
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,"Students");
        //end setup appbar;

        recyclerView=findViewById(R.id.studentListRecyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addNewStudentButton=findViewById(R.id.addNewStudentFloatingButtonId);
        progressDialog=new ProgressDialog(this);
        customDialog=new CustomDialog(this);

    }

    private void showAddNewStudentDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(StudentListActivity.this);
        View view=getLayoutInflater().inflate(R.layout.add_student_dialog,null);
        builder.setView(view);

        EditText nameEt=view.findViewById(R.id.asd_nameEt);
        EditText rollEt=view.findViewById(R.id.asd_rollEt);
        EditText regEt=view.findViewById(R.id.asd_regEt);
        EditText phoneEt=view.findViewById(R.id.asd_phoneEt);
        EditText emailEt=view.findViewById(R.id.asd_emailEt);
        Button saveButton=view.findViewById(R.id.asd_createStudentButton);
        Button cancelButton=view.findViewById(R.id.cancelStudentDialogButtonId);
        TextView titleTv=view.findViewById(R.id.asd_TitleTvId);
        final AlertDialog dialog=builder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=nameEt.getText().toString();
                String roll=rollEt.getText().toString();
                String reg=regEt.getText().toString();
                 phone=phoneEt.getText().toString();
                 email=emailEt.getText().toString();

                if(name.isEmpty()){
                    nameEt.setError("Enter Student Name.");
                    nameEt.requestFocus();
                }else if(roll.isEmpty()){
                    rollEt.setError("Enter Roll");
                    rollEt.requestFocus();
                }else if(reg.isEmpty()){
                    regEt.setError("Enter Reg.");
                    regEt.requestFocus();
                }else{
                    createStudent(name,roll.trim(),reg.trim(),dialog);
                }



            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }
    private void showUpdateStudentDialog(Student student) {
        AlertDialog.Builder builder=new AlertDialog.Builder(StudentListActivity.this);
        View view=getLayoutInflater().inflate(R.layout.add_student_dialog,null);
        builder.setView(view);

        EditText nameEt=view.findViewById(R.id.asd_nameEt);
        EditText rollEt=view.findViewById(R.id.asd_rollEt);
        EditText regEt=view.findViewById(R.id.asd_regEt);
        EditText phoneEt=view.findViewById(R.id.asd_phoneEt);
        EditText emailEt=view.findViewById(R.id.asd_emailEt);
        Button saveButton=view.findViewById(R.id.asd_createStudentButton);
        Button cancelButton=view.findViewById(R.id.cancelStudentDialogButtonId);
        TextView titleTv=view.findViewById(R.id.asd_TitleTvId);
        TextView rollTitle=view.findViewById(R.id.asd_rollTitle);

        titleTv.setText("Update Student.");
        saveButton.setText("Update Student");
        nameEt.setText(""+student.getName());
        rollEt.setVisibility(View.GONE);
        rollTitle.setVisibility(View.GONE);
        regEt.setText(""+student.getRegistration());
        phoneEt.setText(""+student.getPhone());
        emailEt.setText(""+student.getEmail());

        final AlertDialog dialog=builder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=nameEt.getText().toString();
                String reg=regEt.getText().toString();
                 phone=phoneEt.getText().toString();
                 email=emailEt.getText().toString();

                if(name.isEmpty()){
                    nameEt.setError("Enter Student Name.");
                    nameEt.requestFocus();
                }else if(reg.isEmpty()){
                    regEt.setError("Enter Reg.");
                    regEt.requestFocus();
                }else {
                    updateStudent(name, student.getRoll(), reg.trim(), dialog,student.getStudentId());
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }
    private void createStudent(String name, String roll, String reg, AlertDialog dialog) {
        progressDialog.setMessage("Creating New Student...");
        progressDialog.setTitle("Please Wait.");
        progressDialog.show();
        String studentId=ApiRef.studentRef.push().getKey();
        Student student=new Student(studentId,batchId,roll,reg,name,phone,email,"");
        ApiRef.studentRef.child(studentId)
                .setValue(student)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            progressDialog.dismiss();
                            Toast.makeText(StudentListActivity.this, "Student Create Successful.", Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(StudentListActivity.this, "Student Create Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void updateStudent(String name,String roll, String reg, AlertDialog dialog,String studentId) {
        progressDialog.setMessage("Updating Student...");
        progressDialog.setTitle("Please Wait.");
        progressDialog.show();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("name",name);
        hashMap.put("phone",phone);
        hashMap.put("email",email);
        hashMap.put("registration",reg);

        ApiRef.studentRef.child(studentId)
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            progressDialog.dismiss();
                            Toast.makeText(StudentListActivity.this, "Student Update Successful.", Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(StudentListActivity.this, "Student Update Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    private void deleteStudent(Student student, AlertDialog dialog) {
        progressDialog.setMessage("Deleting Student.");
        progressDialog.setTitle("Please Wait.");
        progressDialog.show();

        ApiRef.studentRef.child(student.getStudentId())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            progressDialog.dismiss();
                            Toast.makeText(StudentListActivity.this, "Student Deleted.", Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(StudentListActivity.this, "Student Delete Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}