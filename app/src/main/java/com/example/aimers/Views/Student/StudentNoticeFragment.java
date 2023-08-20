package com.example.aimers.Views.Student;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.aimers.Adapter.NoticeListAdapter;
import com.example.aimers.LocalDb.UserDb;
import com.example.aimers.Model.Notice;
import com.example.aimers.R;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.api.ApiRef;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentNoticeFragment extends Fragment {



    public StudentNoticeFragment() {
        // Required empty public constructor
    }


    private RecyclerView recyclerView;

    private NoticeListAdapter noticeListAdapter;
    private List<Notice> noticeList = new ArrayList<>();

    private ProgressDialog progressDialog;
    private CustomDialog customDialog;
    private UserDb userDb;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_student_notice, container, false);

        init(view);
        noticeListAdapter = new NoticeListAdapter(getContext(), noticeList,true);
        recyclerView.setAdapter(noticeListAdapter);

        return  view;
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

        progressDialog = new ProgressDialog(getContext());
        customDialog = new CustomDialog(getActivity());
        userDb=new UserDb(getActivity());
    }
}