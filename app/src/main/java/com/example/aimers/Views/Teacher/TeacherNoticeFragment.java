package com.example.aimers.Views.Teacher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.aimers.Adapter.NoticeListAdapter;
import com.example.aimers.Interfaces.CustomDialogClickListner;
import com.example.aimers.LocalDb.UserDb;
import com.example.aimers.Model.Notice;
import com.example.aimers.R;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.Views.Admin.CreateNoticeActivity;
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

public class TeacherNoticeFragment extends Fragment {

    public TeacherNoticeFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private FloatingActionButton addNewNoticeButton;

    private NoticeListAdapter noticeListAdapter;
    private List<Notice> noticeList = new ArrayList<>();

    private ProgressDialog progressDialog;
    private CustomDialog customDialog;
    private UserDb userDb;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_notice, container, false);
        init(view);
        noticeListAdapter = new NoticeListAdapter(getContext(), noticeList);
        recyclerView.setAdapter(noticeListAdapter);

        addNewNoticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateNoticeActivity.class);
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
                        if(!notice.getTeacherId().isEmpty() && notice.getTeacherId().equals(userDb.getTeacherData().getPhone())){
                                dialog.dismiss();
                                deleteNotice(notice);
                        }else{
                            dialog.dismiss();
                            Toast.makeText(getContext(), "You Don't Have Any Permission To Delete This Notice.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNegativeButtonClicked(View view, AlertDialog dialog) {
                        dialog.dismiss();
                    }
                });
            }
        });

        return view;

    }


    @Override
    public void onStart() {
        super.onStart();

        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("");
        progressDialog.show();


        ApiRef.noticeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    noticeList.clear();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        Notice notice = snapshot1.getValue(Notice.class);
                        noticeList.add(notice);

                    }
                    Collections.reverse(noticeList);
                    noticeListAdapter.notifyDataSetChanged();
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "" + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void init(View view) {
        recyclerView = view.findViewById(R.id.noticeListRecyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        addNewNoticeButton = view.findViewById(R.id.addNewNoticeFloatingButtonId);
        progressDialog = new ProgressDialog(getContext());
        customDialog = new CustomDialog(getActivity());
        userDb=new UserDb(getActivity());
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
                            Toast.makeText(getContext(), "Notice Delete Successful", Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Notice Delete Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
}