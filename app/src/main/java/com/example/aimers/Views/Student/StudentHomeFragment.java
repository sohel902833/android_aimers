package com.example.aimers.Views.Student;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.aimers.Adapter.StudentClassListAdapter;
import com.example.aimers.LocalDb.UserDb;
import com.example.aimers.Model.Student;
import com.example.aimers.Model.TeacherClass;
import com.example.aimers.R;
import com.example.aimers.api.ApiRef;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class StudentHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private StudentClassListAdapter studentClassListAdapter;
    private List<TeacherClass> teacherClassList=new ArrayList<>();
    private ProgressDialog progressDialog;
    private UserDb userDb;

    public StudentHomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_student_home, container, false);
        init(view);

        studentClassListAdapter=new StudentClassListAdapter(getContext(),teacherClassList);
        recyclerView.setAdapter(studentClassListAdapter);


        studentClassListAdapter.setOnItemClickListner(new StudentClassListAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position, TeacherClass teacherClass) {
                Intent intent=new Intent(getActivity(),StudentAttendanceProfile.class);
                intent.putExtra("classId",teacherClass.getClassId());
                intent.putExtra("subjectName",teacherClass.getSubjectName());
                intent.putExtra("subjectCode",teacherClass.getSubjectCode());
                intent.putExtra("teacherPhone",teacherClass.getTeacherPhone());

                startActivity(intent);
            }
        });

        return  view;
    }
    private void init(View view){
        recyclerView=view.findViewById(R.id.studentClassListRecyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressDialog=new ProgressDialog(getContext());
        userDb=new UserDb(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();

        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Student student=userDb.getStudentData();
        ApiRef.attendanceRef.child(student.getRoll())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                                teacherClassList.clear();

                                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                    String classId= dataSnapshot1.getKey();

                                    ApiRef.classRef.child(classId)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if(dataSnapshot.exists()){
                                                        TeacherClass teacherClass=dataSnapshot.getValue(TeacherClass.class);
                                                        teacherClassList.add(teacherClass);
                                                        studentClassListAdapter.notifyDataSetChanged();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });





                                }
                                progressDialog.dismiss();
                                studentClassListAdapter.notifyDataSetChanged();


                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Right Now You Don't Have Any Class To Show.", Toast.LENGTH_SHORT).show();
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