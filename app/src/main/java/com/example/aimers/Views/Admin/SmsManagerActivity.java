package com.example.aimers.Views.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.aimers.Adapter.BatchSpinnerAdapter;
import com.example.aimers.Adapter.DepartmentSpinnerAdapter;
import com.example.aimers.Model.Batch;
import com.example.aimers.Model.Class;
import com.example.aimers.Model.Student;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.api.ApiRef;
import com.example.aimers.utill.SmsSender;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class SmsManagerActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBar appBar;
    private EditText messageBoxEt;
    private Button sendButton,moreOptionButton;
    private static final int REQUEST_SMS_PERMISSION = 123;
    private ProgressDialog progressDialog;

    private List<Batch> batchList=new ArrayList<>();
    private List<Student> studentList=new ArrayList<>();
    private List<Student> sortedStudentList=new ArrayList<>();
    Spinner batchSpinner;
    BatchSpinnerAdapter batchSpinnerAdapter;
    Batch selectedBatch;
    boolean batchFirst=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_manager);
        init();

         batchSpinnerAdapter=new BatchSpinnerAdapter(this,batchList);
        batchSpinner.setAdapter(batchSpinnerAdapter);


        // Check if the SEND_SMS permission is granted or not
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, proceed with sending SMS
        } else {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    REQUEST_SMS_PERMISSION);
        }



        batchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(batchFirst){
                    batchFirst=false;
                }else {
                    selectedBatch=batchList.get(i);
                    findStudent();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        moreOptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SmsManagerActivity.this, BatchListActivity.class);
                intent.putExtra("target","sms");
                startActivity(intent);
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=messageBoxEt.getText().toString();

                if(message.isEmpty()){
                    Toast.makeText(SmsManagerActivity.this, "Please enter your message", Toast.LENGTH_SHORT).show();
                }else{
                    sendSMS(message);
                }

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("");
        progressDialog.show();

        ApiRef.batchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    batchList.clear();
                    batchList.add(new Batch("all","All","all","B","a"));
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        Batch batch=dataSnapshot1.getValue(Batch.class);
                        batchList.add(batch);
                    }
                    batchSpinnerAdapter.notifyDataSetChanged();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(SmsManagerActivity.this, "No Batch Found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(SmsManagerActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

ApiRef.studentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if(snapshot.exists()){
                    studentList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        Student student=snapshot1.getValue(Student.class);
                        studentList.add(student);

                    }
                }else{
                    Toast.makeText(SmsManagerActivity.this, "No Student Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(SmsManagerActivity.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void init(){
        //setup appbar
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,"Sms Manager");
        appBar.hideBackButton();
        //end setup appbar;

       messageBoxEt=findViewById(R.id.messageboxEt);
       sendButton=findViewById(R.id.sendSmsButton);
        moreOptionButton=findViewById(R.id.sendSmsSeparetly);
       batchSpinner=findViewById(R.id.batchSpinnerId);
      progressDialog=new ProgressDialog(this);

    }

    private void findStudent(){
         if(selectedBatch!=null){
             sortedStudentList.clear();
             for(Student student:studentList){
                  if(selectedBatch.getBatchId().equals("all")){
                      sortedStudentList.add(student);
                  }else if(student.getBatchId().equals(selectedBatch.getBatchId())){
                      sortedStudentList.add(student);
                  }
             }
             Toast.makeText(this, sortedStudentList.size()+" Student Found For This Batch", Toast.LENGTH_SHORT).show();
         }
    }

    private void sendSMS(String message) {
        SmsManager smsManager = SmsManager.getDefault();
        for (Student student : sortedStudentList) {
            String number=student.getPhone();
            if(number!=null && !number.equals("")){
                SmsSender.sendSMSInBackground(SmsManagerActivity.this,number,message,student.getName());
//                try {
////                    smsManager.sendTextMessage(number, null, message, null, null);
//                    ArrayList<String> parts = smsManager.divideMessage(message);
//                    smsManager.sendMultipartTextMessage(number,null,parts,null,null);
//                    Toast.makeText(SmsManagerActivity.this, "SMS sent to " + student.getName(), Toast.LENGTH_SHORT).show();
//                } catch (Exception e) {
//                    Toast.makeText(SmsManagerActivity.this, "SMS failed to " + student.getName(), Toast.LENGTH_SHORT).show();
//                    e.printStackTrace();
//                }
            }else{
                Toast.makeText(this, "Number not found for ("+student.getName()+")", Toast.LENGTH_SHORT).show();
            }

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with sending SMS
            } else {
                // Permission denied, inform the user
                Toast.makeText(this, "SEND_SMS permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}