package com.example.aimers.Views.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
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

import com.example.aimers.Adapter.BatchListAdapter;
import com.example.aimers.Adapter.DepartmentSpinnerAdapter;
import com.example.aimers.Adapter.SpinnerArrayAdapter;
import com.example.aimers.Adapter.SpinnerListAdapter;
import com.example.aimers.Interfaces.CustomDialogClickListner;
import com.example.aimers.Model.Batch;
import com.example.aimers.Model.Class;
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
import java.util.HashMap;
import java.util.List;

public class BatchListActivity extends AppCompatActivity {


    String groupList[]={"A","B"};
    List<String> sessionList=new ArrayList<>();
    List<Class> departmentList=new ArrayList<>();
    private List<Batch> batchList=new ArrayList<>();
    private CustomDialog customDialog;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private FloatingActionButton addNewBatchButton;
    private Toolbar toolbar;
    private AppBar appBar;
    private BatchListAdapter batchListAdapter;

    //initialize batch variables
    String departmentId="",group="",session="",batchName="",target="";


    boolean departmentFirst=true,groupFirst=true,shiftFirst=true,semesterFirst=true,sessionFirst=true;

    String editBatchId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_list);
        target=getIntent().getStringExtra("target");

        init();
        sessionList.clear();
        for(int i=2012; i<=2040; i++){
            sessionList.add(""+i);
        }


        addNewBatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNewBatchDialog();
            }
        });

        batchListAdapter=new BatchListAdapter(this,batchList);
        recyclerView.setAdapter(batchListAdapter);

        batchListAdapter.setOnItemClickListner(new BatchListAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {
                if(target.equals("student")){
                    Intent intent=new Intent(BatchListActivity.this,StudentListActivity.class);
                    intent.putExtra("batchId",batchList.get(position).getBatchId());
                    startActivity(intent);
                }else if(target.equals("sms")){
                    Intent intent=new Intent(BatchListActivity.this,StudentMessageManagerAcitivity.class);
                    intent.putExtra("batchId",batchList.get(position).getBatchId());
                    intent.putExtra("batchName",batchList.get(position).getBatchName());
                    startActivity(intent);
                }else{
                    Intent intent=new Intent(BatchListActivity.this,BatchPaymentActivity.class);
                    intent.putExtra("batchId",batchList.get(position).getBatchId());
                    intent.putExtra("batchName",batchList.get(position).getBatchName());
                    startActivity(intent);
                }

            }

            @Override
            public void onEdit(int position, Batch batch) {
                    showUpdateBatchDialog(batch);
            }

            @Override
            public void onDelete(int position, Batch batch) {
                   customDialog.show("Are You Sure? You Want To Delete This Batch?\n Notice: All Student Will Delete Inside This Batch");
                   customDialog.onActionClick(new CustomDialogClickListner() {
                       @Override
                       public void onPositiveButtonClicked(View view, AlertDialog dialog) {
                           deleteBatch(batch,dialog);
                       }

                       @Override
                       public void onNegativeButtonClicked(View view, AlertDialog dialog) {
                            dialog.dismiss();
                       }
                   });
            }
        });

    }


    private  void init(){

             appBar=new AppBar(this);
             toolbar=findViewById(R.id.appBarId);
             appBar.init(toolbar,"Batch List");

            progressDialog=new ProgressDialog(this);
            addNewBatchButton=findViewById(R.id.addNewBatchFloatingButtonId);
            recyclerView=findViewById(R.id.batchListRecyclerViewId);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));


            customDialog=new CustomDialog(this);



     }
    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("");
        progressDialog.show();

        ApiRef.departmentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if(snapshot.exists()){
                    departmentList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        Class department=snapshot1.getValue(Class.class);
                        departmentList.add(department);
                    }
                }else{
                    Toast.makeText(BatchListActivity.this, "No Department Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(BatchListActivity.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        ApiRef.batchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    batchList.clear();
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        Batch batch=dataSnapshot1.getValue(Batch.class);
                        batchList.add(batch);
                        batchListAdapter.notifyDataSetChanged();
                    }
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(BatchListActivity.this, "No Batch Found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(BatchListActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void showUpdateBatchDialog(Batch batch) {
        departmentFirst=true;groupFirst=true;semesterFirst=true;shiftFirst=true;sessionFirst=true;
        AlertDialog.Builder builder=new AlertDialog.Builder(BatchListActivity.this);
        View view=getLayoutInflater().inflate(R.layout.create_batch_dialog,null);
        builder.setView(view);

        Button saveButton=view.findViewById(R.id.addNewBatchButtonId);
        Button cancelButton=view.findViewById(R.id.cancelBatchDialogButtonId);
        TextView titleTv=view.findViewById(R.id.b_d_TitleTvId);
        TextView departmentTitle=view.findViewById(R.id.b_d_departmentTitleTv);
        TextView groupTitle=view.findViewById(R.id.b_d_groupTitleTv);
        TextView sessionTitle=view.findViewById(R.id.b_d_sessionTitleTv);
        EditText  batchNameEt=view.findViewById(R.id.batchNameEt);

        titleTv.setText("Update Batch");
        groupTitle.setText(groupTitle.getText()+"("+batch.getGroup()+")");
        sessionTitle.setText(sessionTitle.getText()+"("+batch.getSession()+")");
        batchNameEt.setText(batch.getBatchName()+"");

        saveButton.setText("Update");
        Spinner departmentSpinner=view.findViewById(R.id.b_d_departmentSpinnerId);
        Spinner groupSpinner=view.findViewById(R.id.b_d_groupSpinnerId);
        Spinner sessionSpinner=view.findViewById(R.id.b_d_sessionSpinnerId);

        ApiRef.departmentRef.child(batch.getClassId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Class department=dataSnapshot.getValue(Class.class);
                    departmentTitle.setText(departmentTitle.getText()+"("+department.getClassName()+")");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SpinnerArrayAdapter groupAdapter=new SpinnerArrayAdapter(this,groupList);
        SpinnerListAdapter sessionAdapter=new SpinnerListAdapter(this,sessionList);
        DepartmentSpinnerAdapter departmentAdapter=new DepartmentSpinnerAdapter(this,departmentList);

        groupSpinner.setAdapter(groupAdapter);
        sessionSpinner.setAdapter(sessionAdapter);
        departmentSpinner.setAdapter(departmentAdapter);



        final AlertDialog dialog=builder.create();
        dialog.show();

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(departmentFirst){
                    departmentFirst=false;
                }else {
                    departmentId = departmentList.get(i).getClassId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(groupFirst){
                    groupFirst=false;
                }else {
                    group = groupList[i];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sessionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(sessionFirst){
                    sessionFirst=false;
                }else{
                    session=sessionList.get(i);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                batchName=batchNameEt.getText().toString();
                if(batchName.isEmpty()){
                    batchNameEt.setError("Please enter batch name");
                    batchNameEt.requestFocus();
                }else if(departmentId.isEmpty() && group.isEmpty()  && session.isEmpty() && batchName.isEmpty()){
                    Toast.makeText(BatchListActivity.this, "No Change Found.", Toast.LENGTH_SHORT).show();
                }else{
                    editBatchId=batch.getBatchId();

                    if(departmentId.isEmpty()){
                        departmentId=batch.getClassId();
                    }
                    if(group.isEmpty()){
                        group=batch.getGroup();
                    }
                    if(session.isEmpty()){
                        session=batch.getSession();
                    }

                    checkExists(dialog,"update");
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
    private void showAddNewBatchDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(BatchListActivity.this);
        View view=getLayoutInflater().inflate(R.layout.create_batch_dialog,null);
        builder.setView(view);

        Button saveButton=view.findViewById(R.id.addNewBatchButtonId);
        Button cancelButton=view.findViewById(R.id.cancelBatchDialogButtonId);
        TextView titleTv=view.findViewById(R.id.b_d_TitleTvId);
        Spinner departmentSpinner=view.findViewById(R.id.b_d_departmentSpinnerId);
        Spinner groupSpinner=view.findViewById(R.id.b_d_groupSpinnerId);
        Spinner sessionSpinner=view.findViewById(R.id.b_d_sessionSpinnerId);
        EditText  batchNameEt=view.findViewById(R.id.batchNameEt);

        SpinnerArrayAdapter groupAdapter=new SpinnerArrayAdapter(this,groupList);
        SpinnerListAdapter sessionAdapter=new SpinnerListAdapter(this,sessionList);
        DepartmentSpinnerAdapter departmentAdapter=new DepartmentSpinnerAdapter(this,departmentList);

        groupSpinner.setAdapter(groupAdapter);
        sessionSpinner.setAdapter(sessionAdapter);
        departmentSpinner.setAdapter(departmentAdapter);



        final AlertDialog dialog=builder.create();
        dialog.show();

        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    departmentId = departmentList.get(i).getClassId();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                group=groupList[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sessionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                session=sessionList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                batchName=batchNameEt.getText().toString();

                if(batchName.isEmpty()){
                    batchNameEt.setError("Please enter batch name");
                    batchNameEt.requestFocus();
                }else if(departmentId.isEmpty()){
                    Toast.makeText(BatchListActivity.this, "Please Select Class", Toast.LENGTH_SHORT).show();
                }else if(group.isEmpty()){
                    Toast.makeText(BatchListActivity.this, "Please Select Group", Toast.LENGTH_SHORT).show();
                }else if(session.isEmpty()){
                    Toast.makeText(BatchListActivity.this, "Please Select Session", Toast.LENGTH_SHORT).show();
                }else{
                    checkExists(dialog,"create");
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
    private void checkExists(AlertDialog dialog,String action) {
        if(action.equals("update")){
            progressDialog.setMessage("Updating Batch..");
        }else{
            progressDialog.setMessage("Creating New Batch..");
        }
        progressDialog.setTitle("Please Wait...");

        ApiRef.batchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        boolean exists=false;
                         for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                             Batch batch=dataSnapshot1.getValue(Batch.class);
                             assert batch != null;
                             if(batch.getClassId().equals(departmentId) && batch.getGroup().equals(group)  && batch.getSession().equals(session) && batch.getBatchName().equals(batchName)){
                                 exists=true;
                                 break;
                             }
                         }

                         if(exists){
                             //already one batch exists with this same info
                             progressDialog.dismiss();
                             Toast.makeText(BatchListActivity.this, "Already One Batch Created Using This Details.", Toast.LENGTH_SHORT).show();
                         }else{
                             if(action.equals("update")){
                                 updateBatch(dialog);
                             }else{
                                 createNewBatch(dialog);
                             }

                         }

                    }else{
                        //no batch exists create
                        if(action.equals("update")){
                            updateBatch(dialog);
                        }else{
                            createNewBatch(dialog);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.dismiss();
                    Toast.makeText(BatchListActivity.this, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });




    }
    private void createNewBatch(AlertDialog dialog) {
        String batchId=ApiRef.batchRef.push().getKey();
        Batch batch=new Batch(batchId,batchName,departmentId,group,session);

        ApiRef.batchRef.child(batchId)
                .setValue(batch)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            departmentId="";
                            group="";
                            session="";
                            progressDialog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(BatchListActivity.this, "New Batch Created Successful.", Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(BatchListActivity.this, "Batch Create Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    private void updateBatch(AlertDialog dialog) {

        HashMap<String,Object> updateMap=new HashMap<>();
        updateMap.put("departmentId",departmentId);
        updateMap.put("group",group);
        updateMap.put("session",session);
        updateMap.put("batchName",batchName);


        ApiRef.batchRef.child(editBatchId)
                .updateChildren(updateMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            departmentId="";
                            group="";
                            session="";
                            progressDialog.dismiss();
                            dialog.dismiss();
                            Toast.makeText(BatchListActivity.this, "Batch Updated Successful.", Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(BatchListActivity.this, "Batch Update Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    private void deleteBatch(Batch batch, AlertDialog dialog) {
        progressDialog.setMessage("Deleting Batch.");
        progressDialog.setTitle("Please Wait..");
        progressDialog.show();
        ApiRef.batchRef.child(batch.getBatchId())
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        dialog.dismiss();
                         if(task.isSuccessful()){
                             batchListAdapter.notifyDataSetChanged();
                             Toast.makeText(BatchListActivity.this, "Batch Deleted Successful.", Toast.LENGTH_SHORT).show();
                         }else{
                             Toast.makeText(BatchListActivity.this, "Batch Delete Failed.", Toast.LENGTH_SHORT).show();
                         }
                    }
                });

    }

}