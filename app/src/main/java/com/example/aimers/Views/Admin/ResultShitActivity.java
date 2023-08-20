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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aimers.Adapter.NewClassSpinnerAdapter;
import com.example.aimers.Adapter.ResultShitAdapter;
import com.example.aimers.Interfaces.CustomDialogClickListner;
import com.example.aimers.Model.Attendance;
import com.example.aimers.Model.Batch;
import com.example.aimers.Model.Class;
import com.example.aimers.Model.NewClassModel;
import com.example.aimers.Model.ResultShit;
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

public class ResultShitActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBar appBar;
    private RecyclerView recyclerView;
    private FloatingActionButton addNewResultShitButton;

    private ResultShitAdapter resultShitAdapter;
    private List<ResultShit> resultShitList=new ArrayList<>();
    List<Batch> batchList=new ArrayList<>();
    List<Class> departmentList=new ArrayList<>();
    List<NewClassModel> newClassList=new ArrayList<>();

    private ProgressDialog progressDialog;
    private CustomDialog customDialog;


    String selectedBatchId="",selectedDepartmentId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_shit);

        init();
        resultShitAdapter=new ResultShitAdapter(this,resultShitList);

        recyclerView.setAdapter(resultShitAdapter);

        addNewResultShitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 showAddNewResultShitModal();
            }
        });


        resultShitAdapter.setOnItemClickListner(new ResultShitAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {
                ResultShit shit=resultShitList.get(position);
                Intent intent=new Intent(ResultShitActivity.this,AddMarkActivity.class);
                intent.putExtra("shitId",shit.getResultShitId());
                intent.putExtra("shitName",shit.getName());
                intent.putExtra("batchId",shit.getBatchId());
//                intent.putExtra("departmentId",shit.getDepartmentId());
                startActivity(intent);
            }

            @Override
            public void onDelete(int position, ResultShit shit) {
                    customDialog.show("Are You Sure You Want To Delete This Result Shit?.");
                    customDialog.onActionClick(new CustomDialogClickListner() {
                        @Override
                        public void onPositiveButtonClicked(View view, AlertDialog dialog) {
                            progressDialog.setMessage("Deleting Shit.");
                            progressDialog.show();

                            ApiRef.resultShitRef.child(shit.getResultShitId())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            dialog.dismiss();
                                            if(task.isComplete()) {
                                                Toast.makeText(ResultShitActivity.this, "Shit Deleted.", Toast.LENGTH_SHORT).show();
                                            }else{
                                                Toast.makeText(ResultShitActivity.this, "Shit Delete Failed.", Toast.LENGTH_SHORT).show();
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
    public void onStart() {
        super.onStart();

        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("");
        progressDialog.show();


        ApiRef.resultShitRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if(snapshot.exists()){
                    resultShitList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        ResultShit data=snapshot1.getValue(ResultShit.class);
                        resultShitList.add(data);
                    }
                    resultShitAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(ResultShitActivity.this, "No Shit Found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(ResultShitActivity.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        //getBatchList();


        ApiRef.departmentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    departmentList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        Class department=snapshot1.getValue(Class.class);
                        departmentList.add(department);
                    }
                    getBatchListAndBind();
                }else{
                    Toast.makeText(ResultShitActivity.this, "No Class Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(ResultShitActivity.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBatchListAndBind() {
        ApiRef.batchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    batchList.clear();
                    newClassList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        Batch batch=snapshot1.getValue(Batch.class);
                        for(Class department:departmentList){
                            if(department.getClassId().equals(batch.getClassId())){
                                NewClassModel newClass=new NewClassModel(batch.getClassId(),department.getClassName(),batch.getBatchId(),batch.getBatchName(),batch.getGroup(),batch.getSession());
                                newClassList.add(newClass);
                            }
                        }
                    }
                }else{
                    Toast.makeText(ResultShitActivity.this, "No Batch Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(ResultShitActivity.this, "No Batch Found", Toast.LENGTH_SHORT).show();

            }
        });

    }




    public void init() {
        //setup appbar
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,"Result Shits");
        //end setup appbar;

        recyclerView=findViewById(R.id.resultShitRecyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addNewResultShitButton=findViewById(R.id.addNewResultShit);
        progressDialog=new ProgressDialog(this);
        customDialog=new CustomDialog(this);
    }
    private void showAddNewResultShitModal() {
        AlertDialog.Builder builder=new AlertDialog.Builder(ResultShitActivity.this);
        View view=getLayoutInflater().inflate(R.layout.create_resultshit_dialog,null);
        builder.setView(view);

        EditText shitNameEt=view.findViewById(R.id.crd_shitNameEt);
        EditText shitStatusEt=view.findViewById(R.id.crd_shitStatusEt);

        Spinner departmentSpinner=view.findViewById(R.id.crd_departmentSpinner);



        NewClassSpinnerAdapter departmentAdapter=new NewClassSpinnerAdapter(ResultShitActivity.this,newClassList);
        departmentSpinner.setAdapter(departmentAdapter);


        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedBatchId = newClassList.get(i).getBatchId();
                selectedDepartmentId = newClassList.get(i).getDepartmentId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button saveButton=view.findViewById(R.id.addNewBatchButtonId);
        Button cancelButton=view.findViewById(R.id.cancelBatchDialogButtonId);
        TextView titleTv=view.findViewById(R.id.b_d_TitleTvId);

        final AlertDialog dialog=builder.create();
        dialog.show();


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shitName=shitNameEt.getText().toString();
                String shitStatus=shitStatusEt.getText().toString();
                if(shitName.isEmpty()){
                    shitNameEt.setError("Enter Shit Name.");
                    shitNameEt.requestFocus();
                }else if(shitStatus.isEmpty()){
                    shitStatusEt.setError("Enter Shit Status.");
                    shitStatusEt.requestFocus();
                }else if(selectedBatchId.isEmpty()){
                    Toast.makeText(ResultShitActivity.this, "Please Select Batch", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("Creating Result Shit.");
                    progressDialog.show();

                    String shitId=ApiRef.resultShitRef.push().getKey();
                    String currentDateandTime = Attendance.getTodayDate()+ " at "+ Attendance.getCurrentTime();
                    ResultShit resultShit=new ResultShit(shitId,shitName,selectedBatchId,shitStatus);
                    ApiRef.resultShitRef.child(shitId)
                            .setValue(resultShit)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isComplete()) {
                                        progressDialog.dismiss();
                                        dialog.dismiss();
                                        Toast.makeText(ResultShitActivity.this, "Shit Created.", Toast.LENGTH_SHORT).show();
                                    }else {
                                        progressDialog.dismiss();
                                        Toast.makeText(ResultShitActivity.this, "Shit Create Failed.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
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
}