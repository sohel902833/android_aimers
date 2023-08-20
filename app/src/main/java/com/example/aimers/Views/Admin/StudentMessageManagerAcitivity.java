package com.example.aimers.Views.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aimers.Adapter.StudentListAdapter;
import com.example.aimers.Adapter.StudentSmsManagerAdapter;
import com.example.aimers.Interfaces.CustomDialogClickListner;
import com.example.aimers.Model.Student;
import com.example.aimers.Model.StudentSmsModel;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.api.ApiRef;
import com.example.aimers.utill.SmsSender;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentMessageManagerAcitivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBar appBar;
    private RecyclerView recyclerView;
    private FloatingActionButton sendBulkSmsBtn;

    private StudentSmsManagerAdapter studentListAdapter;
    private List<Student> studentList=new ArrayList<>();
    private String batchId="",batchName="";

    private ProgressDialog progressDialog;
    private CustomDialog customDialog;
    private List<StudentSmsModel> studentSmsModelList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_message_manager_acitivity);

        batchId=getIntent().getStringExtra("batchId");
        batchName=getIntent().getStringExtra("batchName");
        init();


        studentListAdapter=new StudentSmsManagerAdapter(this,studentList);
        recyclerView.setAdapter(studentListAdapter);

        sendBulkSmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.show("Send Sms");
                customDialog.onActionClick(new CustomDialogClickListner() {
                    @Override
                    public void onPositiveButtonClicked(View view, AlertDialog dialog) {
                        sendSMS();
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
    private void sendSMS() {
        for (Student student : studentList) {
            String number=student.getPhone();
            String message=student.getMessageText();
            if(number!=null && !number.equals("") && message!=null){
                SmsSender.sendSMSInBackground(StudentMessageManagerAcitivity.this,number,message,student.getName());
//                try {
////                    smsManager.sendTextMessage(number, null, message, null, null);
//                    ArrayList<String> parts = smsManager.divideMessage(message);
//
//                    smsManager.sendMultipartTextMessage(number,null,parts,null,null);
//                    Toast.makeText(StudentMessageManagerAcitivity.this, "SMS sent to " + student.getName(), Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    Toast.makeText(StudentMessageManagerAcitivity.this, "SMS failed to " + student.getName(), Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
            }else{
                Toast.makeText(this, "Number not found or sms is empty for ("+student.getName()+")", Toast.LENGTH_SHORT).show();
            }

        }
        Toast.makeText(this, "Sms sent successfully to all possible number", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(StudentMessageManagerAcitivity.this, "No Student Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(StudentMessageManagerAcitivity.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(){
        //setup appbar
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,"Sms manager-("+batchName+")");
        //end setup appbar;

        recyclerView=findViewById(R.id.studentListRecyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        sendBulkSmsBtn=findViewById(R.id.sendBulkSmsBtn);
        progressDialog=new ProgressDialog(this);
        customDialog=new CustomDialog(this);

    }
}