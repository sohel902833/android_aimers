package com.example.aimers.Views.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.aimers.Adapter.DashboardListAdapter;
import com.example.aimers.LocalDb.UserDb;
import com.example.aimers.Model.DashboardDataModel;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;

import java.util.ArrayList;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity {
    public static  final String DEPARTMENT_TYPE="department";
    public static  final String TEACHER_TYPE="teacher";
    public static  final String STUDENT_TYPE="student";
    public static  final String NOTICE_BOARD_TYPE="noticeboard";
    public static  final String WEB_HOME_PAGE_TYPE="WEBHOMEPAGE";
    public static  final String RESULT_TYPE="result";
    public static  final String PAYMENT_TYPE="payment";
    public static  final String ALL_PAYMENT="all_payment";
    public static  final String LOGOUT_TYPE="logout";
    public static  final String SMS_MANAGER="sms";
    //widgets
    private Toolbar toolbar;
    private AppBar appBar;
    private RecyclerView recyclerView;
    //other variables
    private UserDb userDb;

    private List<DashboardDataModel> dashboardDataList=new ArrayList<>();
    private DashboardListAdapter dashboardListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        init();
        fillDashboardList();
        dashboardListAdapter=new DashboardListAdapter(this,dashboardDataList);
        recyclerView.setAdapter(dashboardListAdapter);


        dashboardListAdapter.setOnItemClickListner(new DashboardListAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {
                DashboardDataModel item= dashboardDataList.get(position);
                if(item.getType().equals(LOGOUT_TYPE)){
                    userDb.logoutUser(AdminMainActivity.this);
                }else if(item.getType().equals(DEPARTMENT_TYPE)){
                    Intent intent=new Intent(AdminMainActivity.this,ClassListActivity.class);
                    startActivity(intent);
                }else if(item.getType().equals(TEACHER_TYPE)){
                    Intent intent=new Intent(AdminMainActivity.this,TeacherListActivity.class);
                    startActivity(intent);
                }else if(item.getType().equals(STUDENT_TYPE)){
                    Intent intent=new Intent(AdminMainActivity.this,BatchListActivity.class);
                    intent.putExtra("target","student");
                    startActivity(intent);
                }else if(item.getType().equals(PAYMENT_TYPE)){
                    Intent intent=new Intent(AdminMainActivity.this,BatchListActivity.class);
                    intent.putExtra("target","payment");
                    startActivity(intent);
                }else if(item.getType().equals(ALL_PAYMENT)){
                    Intent intent=new Intent(AdminMainActivity.this,TotalPaymentsActivity.class);
                    startActivity(intent);
                }else if(item.getType().equals(NOTICE_BOARD_TYPE)){
                    Intent intent=new Intent(AdminMainActivity.this, NoticeListActivity.class);
                    startActivity(intent);
                }else if(item.getType().equals(WEB_HOME_PAGE_TYPE)){
                    Intent intent=new Intent(AdminMainActivity.this, WebHomePage.class);
                    startActivity(intent);
                }else if(item.getType().equals(RESULT_TYPE)){
                    Intent intent=new Intent(AdminMainActivity.this, ResultShitActivity.class);
                    startActivity(intent);
                }else if(item.getType().equals(SMS_MANAGER)){
                    Intent intent=new Intent(AdminMainActivity.this, SmsManagerActivity.class);
                    startActivity(intent);
                }


            }
        });

    }


    private void init(){
        //setup appbar
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,"Dashboard");
        appBar.hideBackButton();
        //end setup appbar;
        recyclerView=findViewById(R.id.adminDashboardRecyclerViewId);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userDb=new UserDb(this);
    }
    private void fillDashboardList() {
        dashboardDataList.add(new DashboardDataModel("Class","See All Class Here",R.drawable.department_icon,DEPARTMENT_TYPE));
        dashboardDataList.add(new DashboardDataModel("Teacher","Manage Teacher",R.drawable.teacher_icon,TEACHER_TYPE));
        dashboardDataList.add(new DashboardDataModel("Student","Manage Students",R.drawable.student_icon,STUDENT_TYPE));
        dashboardDataList.add(new DashboardDataModel("Notice Board","Add New Notice Or Update",R.drawable.notice_board_icon,NOTICE_BOARD_TYPE));
        dashboardDataList.add(new DashboardDataModel("Web Home Page","Edit Web Home Page",R.drawable.notice_board_icon,WEB_HOME_PAGE_TYPE));
        dashboardDataList.add(new DashboardDataModel("Result","handle result",R.drawable.notice_board_icon,RESULT_TYPE));
        dashboardDataList.add(new DashboardDataModel("Payments","Manage payment",R.drawable.notice_board_icon,PAYMENT_TYPE));
        dashboardDataList.add(new DashboardDataModel("All Payments","View Total Payments",R.drawable.notice_board_icon,ALL_PAYMENT));
        dashboardDataList.add(new DashboardDataModel("Sms Manager","Manage Sms",R.drawable.notice_board_icon,SMS_MANAGER));
        dashboardDataList.add(new DashboardDataModel("Logout","Logout User",R.drawable.logout_icon,LOGOUT_TYPE));
    }
}