package com.doyal2020.whatsapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private String recivedUserIID;

    private TextView profileUserName,profileUserStatus;
    private CircleImageView profileIamgesCircleimage;
    private Button sendMessageRequestButton;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        mDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("Users");

        recivedUserIID=getIntent().getExtras().get("visit_user_id").toString();

        initializeFileds();

        retriveUserInfo();

    }

    private void initializeFileds() {

        profileUserName=findViewById(R.id.visit_userName_id);
        profileUserStatus=findViewById(R.id.visit_userStatus_id);
        profileIamgesCircleimage=findViewById(R.id.visit_profile_image_Id);
        sendMessageRequestButton=findViewById(R.id.visit_send_message_Id);

    }
    private void retriveUserInfo() {

        mDatabase.child(recivedUserIID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image"))){

                    String username=dataSnapshot.child("name").getValue().toString();
                    String userstatus=dataSnapshot.child("status").getValue().toString();
                    String userimage=dataSnapshot.child("image").getValue().toString();


                    Picasso.get().load(userimage).placeholder(R.drawable.profile_icon).into(profileIamgesCircleimage);
                    profileUserName.setText(username);
                    profileUserStatus.setText(userstatus);



                }
                else {
                    String username=dataSnapshot.child("name").getValue().toString();
                    String userstatus=dataSnapshot.child("status").getValue().toString();

                    profileUserName.setText(username);
                    profileUserStatus.setText(userstatus);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
