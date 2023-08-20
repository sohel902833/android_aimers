package com.example.aimers.Views.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aimers.Adapter.AddMarkAdapter;
import com.example.aimers.Model.AddMarkStudentModel;
import com.example.aimers.Model.Student;
import com.example.aimers.Model.StudentResultModel;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.api.ApiRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddMarkActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AppBar appBar;
    private RecyclerView recyclerView;
    private EditText totalMarkEt;

    private AddMarkAdapter studentListAdapter;
    private List<Student> studentList=new ArrayList<>();
    private List<AddMarkStudentModel> studentListWithMarks=new ArrayList<>();
    private String batchId="",shitId="",shitName="";
    private ProgressDialog progressDialog;
    private CustomDialog customDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mark);

        batchId=getIntent().getStringExtra("batchId");
        shitId=getIntent().getStringExtra("shitId");
        shitName=getIntent().getStringExtra("shitName");
        init();
        studentListAdapter=new AddMarkAdapter(this,studentListWithMarks,shitId,"");
        recyclerView.setAdapter(studentListAdapter);

        totalMarkEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text=charSequence.toString();
                studentListAdapter=new AddMarkAdapter(AddMarkActivity.this,studentListWithMarks,shitId,text);
                recyclerView.setAdapter(studentListAdapter);
                studentListAdapter.setOnItemClickListner(new AddMarkAdapter.OnItemClickListner() {
                    @Override
                    public void onItemClick(int position) {

                    }

                    @Override
                    public void onEdit(int position, AddMarkStudentModel addMarkStudentModel, String totalMark, String gotMark, String rollPosition, String comment) {
                        editResult(addMarkStudentModel,rollPosition,gotMark,comment,totalMark);
                    }
                });


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        studentListAdapter.setOnItemClickListner(new AddMarkAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onEdit(int position, AddMarkStudentModel addMarkStudentModel, String totalMark, String gotMark, String rollPosition, String comment) {
               editResult(addMarkStudentModel,rollPosition,gotMark,comment,totalMark);
            }
        });



    }

    void editResult(AddMarkStudentModel addMarkStudentModel,String rollPosition,String gotMark,String comment,String totalMark){
        progressDialog.setMessage("Adding Result..");
        progressDialog.show();

        Student student=addMarkStudentModel.getStudent();
        String id="";
        if(addMarkStudentModel.getResult()!=null && !addMarkStudentModel.getResult().getResultId().isEmpty()){
            id=addMarkStudentModel.getResult().getResultId();
        }else{
           id= FirebaseDatabase.getInstance().getReference().push().getKey();
        }
        StudentResultModel studentResultModel=new StudentResultModel(id,student.getStudentId(),student.getBatchId(),shitId,rollPosition,gotMark,comment,totalMark);
        ApiRef.resultRef.child(id)
                .setValue(studentResultModel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if(task.isComplete()){
                            Toast.makeText(AddMarkActivity.this, "Mark Added.", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(AddMarkActivity.this, "Mark Add Failed.", Toast.LENGTH_SHORT).show();
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


        Query query = ApiRef.studentRef
                .orderByChild("batchId")
                .equalTo(batchId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    studentList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        Student student=snapshot1.getValue(Student.class);
                        studentList.add(student);
//                        studentListAdapter.notifyDataSetChanged();
                    }
                    getResultLists(studentList);
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(AddMarkActivity.this, "No Student Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(AddMarkActivity.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


   void getResultLists(List<Student> studentList){
       ApiRef.resultRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               progressDialog.dismiss();
               if(snapshot.exists()){
                   studentListWithMarks.clear();
                   for(Student student:studentList){
                      AddMarkStudentModel addMark=new AddMarkStudentModel();
                      addMark.setStudent(student);
                       for(DataSnapshot snapshot1:snapshot.getChildren()){
                           StudentResultModel result=snapshot1.getValue(StudentResultModel.class);
                           if(result.getStudentId().equals(student.getStudentId()) && result.getShitId().equals(shitId)){
                               addMark.setResult(result);
                           }
                       }
                       studentListWithMarks.add(addMark);
                      studentListAdapter.notifyDataSetChanged();
                   }

               }else{
                   studentListWithMarks.clear();

                   for(Student student:studentList){
                       AddMarkStudentModel addMark=new AddMarkStudentModel();
                       addMark.setStudent(student);

                       studentListWithMarks.add(addMark);
                   }
                   studentListAdapter.notifyDataSetChanged();
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {
               progressDialog.dismiss();
               Toast.makeText(AddMarkActivity.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
           }
       });
    }



    private void init(){
        //setup appbar
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,"Add Marks. In "+"("+shitName+")");
        //end setup appbar;

        recyclerView=findViewById(R.id.studentListRecyclerViewId);
        totalMarkEt=findViewById(R.id.totalMarkEt);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        progressDialog=new ProgressDialog(this);
        customDialog=new CustomDialog(this);

    }
}