package com.example.aimers.Views.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.aimers.Adapter.SliderListAdapter;
import com.example.aimers.Interfaces.CustomDialogClickListner;
import com.example.aimers.LocalDb.UserDb;
import com.example.aimers.Model.CarosalImage;
import com.example.aimers.R;
import com.example.aimers.Services.AppBar;
import com.example.aimers.Services.CustomDialog;
import com.example.aimers.api.ApiRef;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class WebHomePage extends AppCompatActivity {

    private AppBar appBar;
    private Toolbar toolbar;
    private EditText descriptionEt,updateNoticeEt,facebookLinkEt,youtubeLinkEt,imageTitleEt;
    private Button updateButton,chooseImageButton,uploadImageButton;
    private ImageView selectedImageView;

    private ProgressDialog progressDialog;
    private Uri imageUri;
    public static final int PICK_IMAGE=100;

    private StorageReference storageReference;
    private UserDb userDb;
    private List<CarosalImage> sliderImageList=new ArrayList<>();

    private SliderListAdapter sliderListAdapter;
    private RecyclerView sliderRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_home_page);
        init();

        sliderListAdapter=new SliderListAdapter(this,sliderImageList);
        sliderRecyclerView.setAdapter(sliderListAdapter);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description=descriptionEt.getText().toString();
                String updateNotice=updateNoticeEt.getText().toString();
                String facebookLink=facebookLinkEt.getText().toString();
                String youtubeLink=youtubeLinkEt.getText().toString();

                description=description.isEmpty()?"":description;
                updateNotice=updateNotice.isEmpty()?"":updateNotice;
                facebookLink=facebookLink.isEmpty()?"":facebookLink;
                youtubeLink=youtubeLink.isEmpty()?"":youtubeLink;


                progressDialog.setMessage("Updating...");
                progressDialog.show();

                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("description",description);
                hashMap.put("updateNotice",updateNotice);
                hashMap.put("facebookLink",facebookLink);
                hashMap.put("youtubeLink",youtubeLink);

                ApiRef.homePageRef.setValue(hashMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isComplete()){
                                    Toast.makeText(WebHomePage.this, "Successfully Updated.", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(WebHomePage.this, "Data Update Failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });




            }
        });

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imageTitle=imageTitleEt.getText().toString();

                if(imageTitle.isEmpty()){
                    imageTitleEt.setError("Enter Your Image Title.");
                    imageTitleEt.requestFocus();
                }else if(imageUri==null){
                    Toast.makeText(WebHomePage.this, "No Image Found For Upload.", Toast.LENGTH_SHORT).show();
                }
                else{
                    uploadSliderImage(imageTitle);
                }
            }
        });

        sliderListAdapter.setOnItemClickListner(new SliderListAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onEdit(int position, CarosalImage slider) {

            }

            @Override
            public void onDelete(int position, CarosalImage slider) {
                CustomDialog customDialog=new CustomDialog(WebHomePage.this);
                customDialog.show("Are You Sure You Want To Delete This Image From Slider?");
                customDialog.onActionClick(new CustomDialogClickListner() {
                    @Override
                    public void onPositiveButtonClicked(View view, AlertDialog dialog) {
                        progressDialog.setMessage("Deleting Image..");
                        progressDialog.show();
                        StorageReference desertRef = storageReference.child(slider.getImageName());

                        desertRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isComplete()){
                                    ApiRef.homePageRef.child("sliders").child(slider.getImageId())
                                            .removeValue();
                                    dialog.dismiss();
                                    progressDialog.dismiss();
                                }else{
                                    dialog.dismiss();
                                    progressDialog.dismiss();
                                    Toast.makeText(WebHomePage.this, "Image Delete Failed.", Toast.LENGTH_SHORT).show();
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
    protected void onStart() {
        super.onStart();

        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("");
        progressDialog.show();


        ApiRef.homePageRef.child("sliders").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                if(snapshot.exists()){
                    sliderImageList.clear();
                    for(DataSnapshot snapshot1:snapshot.getChildren()){
                        CarosalImage data=snapshot1.getValue(CarosalImage.class);
                        sliderImageList.add(data);
                    }
                    sliderListAdapter.notifyDataSetChanged();
                }else{
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(WebHomePage.this, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        ApiRef.homePageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String description=snapshot.child("description").getValue().toString();
                    String facebookLink=snapshot.child("facebookLink").getValue().toString();
                    String updateNotice=snapshot.child("headLine").getValue().toString();
                    String youtubeLink=snapshot.child("youtubeLink").getValue().toString();

                    descriptionEt.setText(""+description);
                    facebookLinkEt.setText(""+facebookLink);
                    updateNoticeEt.setText(""+updateNotice);
                    youtubeLinkEt.setText(""+youtubeLink);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
              }
        });


    }


    private  void init(){
        toolbar=findViewById(R.id.appBarId);
        appBar=new AppBar(this);
        appBar.init(toolbar,"Web Home Page");
        appBar.hideBackButton();

        descriptionEt=findViewById(R.id.whp_descriptionEt);
        updateNoticeEt=findViewById(R.id.whp_updateNoticeEt);
        facebookLinkEt=findViewById(R.id.whp_facebookLinkEt);
        youtubeLinkEt=findViewById(R.id.whp_youtubeLinkEt);
        imageTitleEt=findViewById(R.id.whp_iamgeTitleEt);
        sliderRecyclerView=findViewById(R.id.carosalImagesRecyclerViewId);
        sliderRecyclerView.setHasFixedSize(true);
        sliderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sliderRecyclerView.setNestedScrollingEnabled(false);

        chooseImageButton=findViewById(R.id.whp_chooseImageButton);
        updateButton=findViewById(R.id.whp_updateButton);
        uploadImageButton=findViewById(R.id.whp_uploadPhotoButton);
        selectedImageView=findViewById(R.id.at_selectedImageId);


        progressDialog=new ProgressDialog(this);
        storageReference= FirebaseStorage.getInstance().getReference().child("Images");
        userDb=new UserDb(this);
    }

    private void uploadSliderImage(String imageTitle) {
        progressDialog.setMessage("Please Wait..");
        progressDialog.setTitle("Saving Your Image.");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String imageName=System.currentTimeMillis() + new Random().nextInt() + "." + getFileExtension(imageUri);

        StorageReference filePath = storageReference.child(imageName);
        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUri = urlTask.getResult();
                saveSlider(imageTitle,imageName,downloadUri.toString());

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(WebHomePage.this, "Slider Image Upload Failed."+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void saveSlider(String imageTitle,String imageName,String imageUri){
        String id=ApiRef.homePageRef.push().getKey();
        CarosalImage carosalImage=new CarosalImage(id,imageName,imageUri,imageTitle);

        ApiRef.homePageRef.child("sliders")
                .child(id)
                .setValue(carosalImage)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isComplete()){
                            progressDialog.dismiss();
                            Toast.makeText(WebHomePage.this, "Slider Image Uploaded.", Toast.LENGTH_SHORT).show();
                        }else{
                            progressDialog.dismiss();
                            Toast.makeText(WebHomePage.this, "Slider Save Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }



    public String getFileExtension(Uri imageuri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageuri));
    }


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