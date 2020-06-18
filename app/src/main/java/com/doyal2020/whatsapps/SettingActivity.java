package com.doyal2020.whatsapps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button updateButton;
    private CircleImageView circleImageView;
    private EditText userName,setUpdate;
    private ProgressDialog mProgressDialog;


    private FirebaseAuth mAth;
    private DatabaseReference mDatabase;
    private StorageReference userProfileStorageRef;
    private  String currentUserID;

    private  Uri imageUri,resultUri;

    private  static  final int GallerPickCode=1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        titileMethod();
        mAth=FirebaseAuth.getInstance();
        currentUserID=mAth.getCurrentUser().getUid();
        mDatabase=FirebaseDatabase.getInstance().getReference("WhatsApp");
        userProfileStorageRef= FirebaseStorage.getInstance().getReference("WhatsApp").child("Profile Images");


        initailizeFileds();

        userName.setVisibility(View.INVISIBLE);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog.setTitle("Updating now......");
                mProgressDialog.setMessage("Please wait..........");
                mProgressDialog.setCanceledOnTouchOutside(true);
                mProgressDialog.show();
                updateSetting();

            }
        });


        retriveUserInformation();

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gelleryIntent=new Intent();
                gelleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                gelleryIntent.setType("image/*");
                startActivityForResult(gelleryIntent,GallerPickCode);

            }
        });

    }
    private void titileMethod() {
        mToolbar=findViewById(R.id.setting_page_toolbar_id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Update setting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
    private void initailizeFileds() {
        updateButton=findViewById(R.id.update_button);
        userName=findViewById(R.id.setting_username_id);
        setUpdate=findViewById(R.id.set_profile_status_Id);
        circleImageView=findViewById(R.id.profile_image_id);

        mProgressDialog=new ProgressDialog(this);

    }

//image cropper activity start

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GallerPickCode  && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);



            if (resultCode==RESULT_OK){

                mProgressDialog.setTitle("Set Profile Image......");
                mProgressDialog.setMessage("Please wait, your profile image is updating ..........");
                mProgressDialog.setCanceledOnTouchOutside(true);
                mProgressDialog.show();

                resultUri=result.getUri();

                StorageReference reference=userProfileStorageRef.child(currentUserID+" .jpg ");


                reference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(SettingActivity.this, "Image store successful", Toast.LENGTH_SHORT).show();

                        //image uri start

                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri downlaodUri=uriTask.getResult();
                        String imagesLink=downlaodUri.toString();

                        //image uri end

                        mDatabase.child("Users").child(currentUserID).child("image").setValue(imagesLink).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    mProgressDialog.dismiss();
                                    Toast.makeText(SettingActivity.this, "Image link insert Database, Successfully....", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        mProgressDialog.dismiss();
                        Toast.makeText(SettingActivity.this, "Error"+e, Toast.LENGTH_SHORT).show();

                    }
                });

                }

        }

    }
    //image cropper activity end

    //FileExtension Here
    public  String getFileExtension(Uri resultUri){

        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(resultUri));

    }

    private void updateSetting() {

        String setUserName=userName.getText().toString();
        String status=setUpdate.getText().toString();

        if (TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please enter your user name", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(status)){
            Toast.makeText(this, "Please enter simple status", Toast.LENGTH_SHORT).show();
        }

        else {

            HashMap<String,Object> profileMap=new HashMap<>();

            profileMap.put("name",setUserName);
            profileMap.put("status",status);
            profileMap.put("uid",currentUserID);

            mDatabase.child("Users").child(currentUserID).updateChildren(profileMap).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                sendToUserMainActivity();
                                Toast.makeText(SettingActivity.this, "Updating is successful", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                String message=task.getException().toString();
                                Toast.makeText(SettingActivity.this, "Error"+message, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


        }


    }
    private void sendToUserMainActivity() {
        Intent intent=new Intent(SettingActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void retriveUserInformation() {



        mDatabase.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image"))) ){

                    String retriveUsername=dataSnapshot.child("name").getValue().toString();
                    String retriveStatus=dataSnapshot.child("status").getValue().toString();
                    String retriveProfileImage=dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(retriveProfileImage).into(circleImageView);
                    userName.setText(retriveUsername);
                    setUpdate.setText(retriveStatus);

                }
                else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name"))){

                    String retriveUsername=dataSnapshot.child("name").getValue().toString();
                    String retriveStatus=dataSnapshot.child("status").getValue().toString();

                    userName.setText(retriveUsername);
                    setUpdate.setText(retriveStatus);

                }
                else {
                    userName.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingActivity.this, "Please set & update your profile information....", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
