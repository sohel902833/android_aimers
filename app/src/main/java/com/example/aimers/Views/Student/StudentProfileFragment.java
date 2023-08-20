package com.example.aimers.Views.Student;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aimers.Interfaces.CustomDialogClickListner;
import com.example.aimers.LocalDb.UserDb;
import com.example.aimers.Model.Student;
import com.example.aimers.R;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.api.ApiRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class StudentProfileFragment extends Fragment {

    public StudentProfileFragment() {
        // Required empty public constructor
    }


    private UserDb userDb;
    private TextView nameTv,emailTv,phoneTv,rollTv,registrationTv;
    Button logoutButton,editProfileButton;
    private ProgressDialog progressDialog;
    private CustomDialog customDialog;


    String phone="",email="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_student_profile, container, false);
        init(view);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Student student=userDb.getStudentData();
                email=student.getEmail();
                phone=student.getPhone();
                showUpdateStudentDialog(student);
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialog.show("Logout?");
                customDialog.onActionClick(new CustomDialogClickListner() {
                    @Override
                    public void onPositiveButtonClicked(View view, AlertDialog dialog) {
                        userDb.logoutUser(getActivity());
                    }

                    @Override
                    public void onNegativeButtonClicked(View view, AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });

            }
        });


        return  view;
    }

    private void init(View view){
        logoutButton=view.findViewById(R.id.studentLogoutButton);
        editProfileButton=view.findViewById(R.id.sp_editProfileButton);
        nameTv=view.findViewById(R.id.sp_studentNameTv);
        emailTv=view.findViewById(R.id.sp_studentEmailTv);
        phoneTv=view.findViewById(R.id.sp_studentPhoneTv);
        rollTv=view.findViewById(R.id.sp_studentRollTv);
        registrationTv=view.findViewById(R.id.sp_studentRegistrationTv);

        userDb=new UserDb(getActivity());
        progressDialog=new ProgressDialog(getContext());
        customDialog=new CustomDialog(getActivity());

    }
    @Override
    public void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        ApiRef.studentRef.child(userDb.getStudentData().getRoll())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            progressDialog.dismiss();
                            Student student=dataSnapshot.getValue(Student.class);
                            userDb.setStudentData(student);
                            nameTv.setText("Name: "+student.getName());
                            phoneTv.setText("Phone: "+student.getPhone());
                            emailTv.setText("Email: "+student.getEmail());
                            rollTv.setText("Roll: "+student.getRoll());
                            registrationTv.setText("Registration: "+student.getRegistration());


                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Teacher Not Found.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showUpdateStudentDialog(Student student) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
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
                }else{
                    updateStudent(name,student.getRoll(),reg.trim(),dialog);
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

    private void updateStudent(String name,String roll, String reg, AlertDialog dialog) {
        progressDialog.setMessage("Updating Student...");
        progressDialog.setTitle("Please Wait.");
        progressDialog.show();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("name",name);
        hashMap.put("phone",phone);
        hashMap.put("email",email);
        hashMap.put("registration",reg);


        ApiRef.studentRef.child(roll)
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialog.dismiss();
                            progressDialog.dismiss();
                            onStart();
                            Toast.makeText(getContext(), "Profile Update Successful.", Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Profile Update Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}