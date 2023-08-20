package com.example.aimers.Views.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.aimers.LocalDb.UserDb;
import com.example.aimers.Model.Attendance;
import com.example.aimers.Model.Notice;
import com.example.aimers.Model.User;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.example.aimers.api.ApiRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class CreateNoticeActivity extends AppCompatActivity {

    private AppBar appBar;
    private Toolbar toolbar;

    private EditText descriptionEt;
    private ImageView selectedImageView;
    private Button chooseImageButton,addNotificationButton;

    private ProgressDialog progressDialog;
    private Uri imageUri;
    public static final int PICK_IMAGE=100;

    private StorageReference storageReference;
    private UserDb userDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notice);

        init();


        addNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description=descriptionEt.getText().toString();

                if(description.isEmpty() && imageUri==null){
                    Toast.makeText(CreateNoticeActivity.this, "Nothing Found For Upload.", Toast.LENGTH_SHORT).show();
                }else{

                    if(imageUri==null){
                        saveNotice(description,"");
                    }else{
                        uploadImage(description);
                    }


                }


            }
        });

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });


    }


    private  void init(){
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,"Create Notice");
        appBar.hideBackButton();

        descriptionEt=findViewById(R.id.na_descriptionEt);
        chooseImageButton=findViewById(R.id.at_chooseNotificationImageButton);
        addNotificationButton=findViewById(R.id.na_addNotificationButton);
        selectedImageView=findViewById(R.id.at_selectedImageId);


        progressDialog=new ProgressDialog(this);
        storageReference= FirebaseStorage.getInstance().getReference().child("NoticeImages");
        userDb=new UserDb(this);
    }


    private void uploadImage(String description) {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setTitle("Saving Your Notice.");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference filePath = storageReference.child(System.currentTimeMillis() + new Random().nextInt() + "." + getFileExtension(imageUri));
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUri = urlTask.getResult();
                saveNotice(description,downloadUri.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(CreateNoticeActivity.this, "Notice Save Failed."+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void saveNotice(String description,String imageUri) {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setTitle("Saving Your Notice.");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String noticeId= ApiRef.noticeRef.push().getKey();
       String currentDateandTime = Attendance.getTodayDate()+ " at "+ Attendance.getCurrentTime();

        String teacherId=userDb.getUserData().getUserType().equals(User.TEACHER)?userDb.getTeacherData().getPhone():"";

        Notice notice=new Notice(noticeId,description,imageUri,currentDateandTime,teacherId);
        ApiRef.noticeRef
                .child(noticeId)
                .setValue(notice)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            finish();
                            Toast.makeText(CreateNoticeActivity.this, "Notice Upload Successful", Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(CreateNoticeActivity.this, "Notice Save Failed."+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public String getFileExtension(Uri imageuri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageuri));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                imageUri=data.getData();
                selectedImageView.setVisibility(View.VISIBLE);
                Picasso.get().load(imageUri).into(selectedImageView);
                chooseImageButton.setText("Image Selected");
            }
        }
    }
}