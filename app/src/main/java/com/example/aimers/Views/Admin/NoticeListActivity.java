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

import com.example.aimers.Adapter.NoticeListAdapter;
import com.example.aimers.Interfaces.CustomDialogClickListner;
import com.example.aimers.Model.Notice;
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
import java.util.Collections;
import java.util.List;

public class NoticeListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AppBar appBar;
    private RecyclerView recyclerView;
    private FloatingActionButton addNewNoticeButton;

    private NoticeListAdapter noticeListAdapter;
    private List<Notice> noticeList=new ArrayList<>();

    private ProgressDialog progressDialog;
    private CustomDialog customDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_list);
        init();
        noticeListAdapter=new NoticeListAdapter(this,noticeList);
        recyclerView.setAdapter(noticeListAdapter);

        addNewNoticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(NoticeListActivity.this,CreateNoticeActivity.class);
                startActivity(intent);
            }
        });


        noticeListAdapter.setOnItemClickListner(new NoticeListAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onEdit(int position, Notice notice) {

            }

            @Override
            public void onDelete(int position, Notice notice) {
                customDialog.show("Are Your Sure? You Want To Delete This Notice?");
                customDialog.onActionClick(new CustomDialogClickListner() {
                    @Override
                    public void onPositiveButtonClicked(View view, AlertDialog dialog) {
                        dialog.dismiss();
                        deleteNotice(notice);
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


        ApiRef.noticeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if(snapshot.exists()){
                    noticeList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        Notice notice=snapshot1.getValue(Notice.class);
                        noticeList.add(notice);
                    }
                    Collections.reverse(noticeList);
                    noticeListAdapter.notifyDataSetChanged();
                }else{
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(NoticeListActivity.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void init(){
        //setup appbar
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,"Notice Board");
        //end setup appbar;

        recyclerView=findViewById(R.id.noticeListRecyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        addNewNoticeButton=findViewById(R.id.addNewNoticeFloatingButtonId);
        progressDialog=new ProgressDialog(this);
        customDialog=new CustomDialog(this);

    }
    private void deleteNotice(Notice notice) {
        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Deleting Notice.");
        progressDialog.show();

        ApiRef.noticeRef.child(notice.getNoticeId())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(NoticeListActivity.this, "Notice Delete Successful", Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(NoticeListActivity.this, "Notice Delete Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}