package com.example.aimers.Views.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.aimers.Adapter.TeacherListAdapter;
import com.example.aimers.Interfaces.CustomDialogClickListner;
import com.example.aimers.Model.Teacher;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.api.ApiRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeacherListActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AppBar appBar;
   private RecyclerView recyclerView;
   private FloatingActionButton addTeacherButton;

   private ProgressDialog progressDialog;
   private List<Teacher> teacherList=new ArrayList<>();
   private TeacherListAdapter teacherListAdapter;
   private CustomDialog customDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_list);

        init();
        teacherListAdapter=new TeacherListAdapter(this,teacherList);
        recyclerView.setAdapter(teacherListAdapter);

        addTeacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(TeacherListActivity.this,AddNewTeacherActivity.class);
                intent.putExtra("action","create");
                intent.putExtra("phone","");
                intent.putExtra("comeFrom","admin");
                startActivity(intent);
            }
        });

        teacherListAdapter.setOnItemClickListner(new TeacherListAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onEdit(int position, Teacher teacher) {
                Intent intent=new Intent(TeacherListActivity.this,AddNewTeacherActivity.class);
                intent.putExtra("action",AddNewTeacherActivity.ACTION_EDIT);
                intent.putExtra("phone",teacher.getPhone());
                intent.putExtra("comeFrom","admin");
                startActivity(intent);
            }

            @Override
            public void onDelete(int position, Teacher teacher) {
                customDialog.show("Are You Sure? You Want To Delete This Teacher");
                customDialog.onActionClick(new CustomDialogClickListner() {
                    @Override
                    public void onPositiveButtonClicked(View view, AlertDialog dialog) {
                            progressDialog.setMessage("Deleting Teacher.");
                            progressDialog.setTitle("Please Wait.");
                            progressDialog.show();

                            ApiRef.teacherRef.child(teacher.getPhone())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if(task.isSuccessful()){
                                                Toast.makeText(TeacherListActivity.this, "Teacher Deleted.", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(TeacherListActivity.this, "Teacher Delete Failed.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });



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

        ApiRef.teacherRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if(snapshot.exists()){
                    teacherList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        Teacher teacher=snapshot1.getValue(Teacher.class);
                        teacherList.add(teacher);
                        teacherListAdapter.notifyDataSetChanged();
                    }
                }else{
                    Toast.makeText(TeacherListActivity.this, "No Teacher Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(TeacherListActivity.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void init(){
        //setup appbar
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,"Teachers");
        appBar.hideBackButton();
        //end setup appbar;

        recyclerView=findViewById(R.id.teacherListRecyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addTeacherButton=findViewById(R.id.addNewTeacherFloatingButtonId);

        progressDialog=new ProgressDialog(this);
        customDialog=new CustomDialog(this);


    }
}