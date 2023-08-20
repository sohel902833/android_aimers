package com.example.aimers.api;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ApiRef {
//    public static  final String DATABASE_URL="https://brightfuture-85a79-default-rtdb.asia-southeast1.firebasedatabase.app";
   public static  final String DATABASE_URL="https://aimers-d9fe4-default-rtdb.firebaseio.com";
    public static final DatabaseReference departmentRef= FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("CollageManagement").child("ClassList");
    public static final DatabaseReference teacherRef= FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("CollageManagement").child("Teachers");
    public static final DatabaseReference studentRef= FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("CollageManagement").child("Student");
    public static final DatabaseReference batchRef= FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("CollageManagement").child("Batches");
    public static final DatabaseReference noticeRef= FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("CollageManagement").child("Notice");
    public static final DatabaseReference classRef= FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("CollageManagement").child("ClassList");
    public static final DatabaseReference teacherClassRef= FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("CollageManagement").child("TeacherClass");
    public static final DatabaseReference attendanceRef= FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("CollageManagement").child("Attendance");
    public static final DatabaseReference homePageRef= FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("CollageManagement").child("BasicInfo");
    public static final DatabaseReference resultShitRef= FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("CollageManagement").child("ResultShit");
    public static final DatabaseReference resultRef= FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("CollageManagement").child("Results");
    public static final DatabaseReference batchPaymentRef= FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("CollageManagement").child("PaymentLists");
    public static final DatabaseReference studentPaymentsRef= FirebaseDatabase.getInstance(DATABASE_URL).getReference().child("CollageManagement").child("StudentPayments");


    //attendance model
      //StudentRoll
            //classId
                 //Date
                     //AttendanceModel

}
