package com.example.aimers.Views.Teacher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aimers.Interfaces.CustomDialogClickListner;
import com.example.aimers.LocalDb.UserDb;
import com.example.aimers.Model.Teacher;
import com.example.aimers.R;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.Views.Admin.AddNewTeacherActivity;
import com.example.aimers.api.ApiRef;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class TeacherProfileFragment extends Fragment {

    public TeacherProfileFragment() {
        // Required empty public constructor
    }

    private UserDb userDb;
    private TextView nameTv,emailTv,phoneTv,codeTv;
    Button logoutButton,editProfileButton;
    private ProgressDialog progressDialog;
    private CustomDialog customDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_teacher_profile, container, false);
        init(view);

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), AddNewTeacherActivity.class);
                intent.putExtra("action",AddNewTeacherActivity.ACTION_EDIT);
                intent.putExtra("phone",userDb.getTeacherData().getPhone());
                intent.putExtra("comeFrom","teacherProfile");
                startActivity(intent);
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
      logoutButton=view.findViewById(R.id.teacherLogoutButtonId);
      editProfileButton=view.findViewById(R.id.tp_editProfileButton);
      nameTv=view.findViewById(R.id.tp_teacherNameTv);
      emailTv=view.findViewById(R.id.tp_teacherEmailTv);
      phoneTv=view.findViewById(R.id.tp_teacherPhoneTv);
      codeTv=view.findViewById(R.id.tp_teacherCodeTv);
      userDb=new UserDb(getActivity());
      progressDialog=new ProgressDialog(getContext());
      customDialog=new CustomDialog(getActivity());
  }
    @Override
    public void onStart() {
        super.onStart();
        progressDialog.setMessage("Loading..");
        progressDialog.show();
        ApiRef.teacherRef.child(userDb.getTeacherData().getPhone())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            progressDialog.dismiss();
                            Teacher teacher=dataSnapshot.getValue(Teacher.class);
                            userDb.setTeacherData(teacher);
                            nameTv.setText("Name: "+teacher.getName());
                            phoneTv.setText("Phone: "+teacher.getPhone());
                            emailTv.setText("Email: "+teacher.getEmail());
                            codeTv.setText("Code: "+teacher.getCode());


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
}