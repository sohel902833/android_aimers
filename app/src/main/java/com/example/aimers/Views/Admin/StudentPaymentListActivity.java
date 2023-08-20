package com.example.aimers.Views.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aimers.Adapter.PaymentBatchAdapter;
import com.example.aimers.Adapter.SpinnerListAdapter;
import com.example.aimers.Adapter.StudentPaymentAdapter;
import com.example.aimers.Model.Payment;
import com.example.aimers.Model.PaymentBatch;
import com.example.aimers.Model.Student;
import com.example.aimers.Model.StudentPaymentModel;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.api.ApiRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class StudentPaymentListActivity extends AppCompatActivity {

    private AppBar appBar;
    private Toolbar toolbar;
    private RecyclerView paymentListRecyclerView;

    private ProgressDialog progressDialog;

    private String batchId="",batchName="",paymentId="",paymentName="",monthlyPayment="";
    private CustomDialog customDialog;

    private List<StudentPaymentModel> studentPaymentModelList=new ArrayList<>();
    private StudentPaymentAdapter studentPaymentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_payment_list);

        batchId=getIntent().getStringExtra("batchId");
        batchName=getIntent().getStringExtra("batchName");
        paymentId=getIntent().getStringExtra("paymentId");
        paymentName=getIntent().getStringExtra("paymentName");
        monthlyPayment=getIntent().getStringExtra("monthlyPayment");
        init();


        studentPaymentAdapter=new StudentPaymentAdapter(this,studentPaymentModelList,Double.parseDouble(monthlyPayment));
        paymentListRecyclerView.setAdapter(studentPaymentAdapter);

        studentPaymentAdapter.setOnItemClickListner(new StudentPaymentAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {
                StudentPaymentModel paymentModel=studentPaymentModelList.get(position);
                showSubmitPaymentModal(paymentModel);
            }
        });

    }

    private void showSubmitPaymentModal(StudentPaymentModel paymentModel) {
        AlertDialog.Builder builder=new AlertDialog.Builder(StudentPaymentListActivity.this);
        View view=getLayoutInflater().inflate(R.layout.submit_payment_dialog,null);
        builder.setView(view);
        Payment payment=paymentModel.getPayment();
        Button saveButton=view.findViewById(R.id.addNewBatchButtonId);
        Button cancelButton=view.findViewById(R.id.cancelBatchDialogButtonId);

        TextView titleTv=view.findViewById(R.id.sb_d_titleTv);
        TextView receivedPaymentTv=view.findViewById(R.id.sp_receivedPaymentTv);
        TextView duePaymentTv=view.findViewById(R.id.sp_duePayment);
        EditText amountEt=view.findViewById(R.id.sb_d_amountEt);

        if(payment!=null){
            titleTv.setText("Update Payment");
            saveButton.setText("Update");
            Double duePayment=Double.parseDouble(monthlyPayment)-payment.getAmount();
            receivedPaymentTv.setText("Received Amount: "+ payment.getAmount());
            duePaymentTv.setText("Due Amount: "+duePayment);
        }else{
            receivedPaymentTv.setText("Received Amount: "+0);
            duePaymentTv.setText("Due Amount: "+monthlyPayment);
        }

        final AlertDialog dialog=builder.create();
        dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount=amountEt.getText().toString();

                if(amount.isEmpty()){
                    amountEt.setError("Please enter amount");
                    amountEt.requestFocus();
                }else if(Double.parseDouble(amount)>Double.parseDouble(monthlyPayment)){
                    Toast.makeText(StudentPaymentListActivity.this, "Amount Can't be greater then total amount", Toast.LENGTH_SHORT).show();
                }else{
                   if(payment!=null){
                       updatePayment(payment,Double.parseDouble(amount),dialog);
                   }else{
                       createPayment(Double.parseDouble(amount),paymentModel.getStudent(),dialog);
                   }
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


    private  void init(){
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,batchName+"("+paymentName+")");
        paymentListRecyclerView=findViewById(R.id.studentPaymentRecyclerViewId);
        paymentListRecyclerView.setHasFixedSize(true);
        paymentListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressDialog=new ProgressDialog(this);
        customDialog=new CustomDialog(this);
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
                    List<Student> studentList=new ArrayList<>();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        Student student=snapshot1.getValue(Student.class);
                        studentList.add(student);
                    }

                    parseWithPaymentList(studentList);

                }else{
                    progressDialog.dismiss();
                    Toast.makeText(StudentPaymentListActivity.this, "No Student Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(StudentPaymentListActivity.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parseWithPaymentList(List<Student> studentList) {
        Query query = ApiRef.studentPaymentsRef
                .orderByChild("paymentBatchId")
                .equalTo(paymentId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if(snapshot.exists()){
                    studentPaymentModelList.clear();
                    for(Student student:studentList) {
                        StudentPaymentModel paymentModel=new StudentPaymentModel();
                        paymentModel.setStudent(student);
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            Payment payment = snapshot1.getValue(Payment.class);
                            if(payment.getStudentId().equals(student.getStudentId())){
                                paymentModel.setPayment(payment);
                            }
                        }
                        studentPaymentModelList.add(paymentModel);
                    }
                    studentPaymentAdapter.notifyDataSetChanged();

                }else{
                    studentPaymentModelList.clear();
                    for(Student student:studentList) {
                        StudentPaymentModel paymentModel=new StudentPaymentModel();
                        paymentModel.setStudent(student);
                        studentPaymentModelList.add(paymentModel);
                    }
                    studentPaymentAdapter.notifyDataSetChanged();
                    Toast.makeText(StudentPaymentListActivity.this, "No one paid on this batch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(StudentPaymentListActivity.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updatePayment(Payment payment, Double amount, Dialog dialog){
        progressDialog.setMessage("Updating Payment.");
        progressDialog.setTitle("Please wait..");
        progressDialog.show();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("amount",amount);

        ApiRef.studentPaymentsRef.child(payment.getPaymentId()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(StudentPaymentListActivity.this, "Payment Updated", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    onStart();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(StudentPaymentListActivity.this, "Payment Update Failed", Toast.LENGTH_SHORT).show();

                }
            }
        });


    }

    private String getCurrentDateInCustomFormat() {
        Date currentDate = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy 'at' hh:mm a", Locale.US);
        String formattedDate = dateFormat.format(currentDate);
       return formattedDate;
    }
    private void createPayment( Double amount,Student student, Dialog dialog){
        progressDialog.setMessage("Creating Payment.");
        progressDialog.setTitle("Please wait..");
        progressDialog.show();

        String payId=ApiRef.studentPaymentsRef.push().getKey();

        Payment payment=new Payment(payId,student.getStudentId(),paymentId,batchId,amount,getCurrentDateInCustomFormat());

        ApiRef.studentPaymentsRef.child(payId).setValue(payment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(StudentPaymentListActivity.this, "Payment Saved Successful", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    onStart();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(StudentPaymentListActivity.this, "Payment Save Failed", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

}