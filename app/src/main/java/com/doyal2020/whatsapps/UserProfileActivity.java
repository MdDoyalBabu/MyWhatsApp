package com.doyal2020.whatsapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private String recivedUserIID,sendUserID,current_state;

    private TextView profileUserName,profileUserStatus;
    private CircleImageView profileIamgesCircleimage;
    private Button sendMessageRequestButton,declineMessageRequestButton;

    private DatabaseReference mDatabase,chatRequestDatabase,contactDatabase;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        mDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("Users");
        chatRequestDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("ChatRequestID");
        contactDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("Contacts");

        recivedUserIID=getIntent().getExtras().get("visit_user_id").toString();
        mAuth=FirebaseAuth.getInstance();
        sendUserID=mAuth.getCurrentUser().getUid();

        initializeFileds();

        retriveUserInfo();

    }

    private void initializeFileds() {

        profileUserName=findViewById(R.id.visit_userName_id);
        profileUserStatus=findViewById(R.id.visit_userStatus_id);
        profileIamgesCircleimage=findViewById(R.id.visit_profile_image_Id);
        sendMessageRequestButton=findViewById(R.id.visit_send_message_Id);
        declineMessageRequestButton=findViewById(R.id.visit_decline_message_Id);
        current_state="new";

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


                    ManageChatRequest();

                }
                else {
                    String username=dataSnapshot.child("name").getValue().toString();
                    String userstatus=dataSnapshot.child("status").getValue().toString();

                    profileUserName.setText(username);
                    profileUserStatus.setText(userstatus);

                    ManageChatRequest();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void ManageChatRequest() {

        chatRequestDatabase.child(sendUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(recivedUserIID)){

                    String request_type1=dataSnapshot.child(recivedUserIID).child("request_type").getValue().toString();

                    if (request_type1.equals("sent")){
                        current_state="request_sent";
                        sendMessageRequestButton.setText("Cancel Chat Request");
                    }
                    else if (request_type1.equals("received")){

                        current_state="request_received";
                        sendMessageRequestButton.setText("Accept Chat Request");

                        declineMessageRequestButton.setVisibility(View.VISIBLE);
                        declineMessageRequestButton.setEnabled(true);

                        declineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cancelChatRequest();
                            }
                        });
                    }
                }


                else {

                    contactDatabase.child(sendUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(recivedUserIID)){
                                current_state="Friends";
                                sendMessageRequestButton.setText("Remove this contact");

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (!sendUserID.equals(recivedUserIID)){

            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendMessageRequestButton.setEnabled(false);

                    if (current_state.equals("new")){
                        sendChatRequest();

                    }
                    if (current_state.equals("request_sent")){
                        cancelChatRequest();

                    }
                    if (current_state.equals("request_received")){
                        AcceptChatRequest();
                    }
                    if (current_state.equals("Friends")){


                        RemoveSpecificContact();
                    }

                }
            });

        }
        else {
            sendMessageRequestButton.setVisibility(View.INVISIBLE);

        }

    }

    private void RemoveSpecificContact() {

        contactDatabase.child(sendUserID).child(recivedUserIID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    contactDatabase.child(recivedUserIID).child(sendUserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                sendMessageRequestButton.setEnabled(true);
                                current_state="new";
                                sendMessageRequestButton.setText("Send Message");

                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                declineMessageRequestButton.setEnabled(false);
                            }

                        }
                    });
                }

            }
        });

    }

    private void AcceptChatRequest() {

        contactDatabase.child(sendUserID).child(recivedUserIID).child("Contact")
                .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    contactDatabase.child(recivedUserIID).child(sendUserID).child("Contact")
                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                chatRequestDatabase.child(sendUserID).child(recivedUserIID)
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){
                                            chatRequestDatabase.child(recivedUserIID).child(sendUserID)
                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if (task.isSuccessful()){

                                                        sendMessageRequestButton.setEnabled(true);
                                                        current_state="Friends";

                                                        sendMessageRequestButton.setText("Remove this contact");

                                                        declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                        declineMessageRequestButton.setEnabled(false);

                                                    }

                                                }
                                            });
                                        }
                                    }
                                });

                            }

                        }
                    });
                }

            }
        });


    }

    private void cancelChatRequest() {

        chatRequestDatabase.child(sendUserID).child(recivedUserIID)
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    chatRequestDatabase.child(recivedUserIID).child(sendUserID)
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                     sendMessageRequestButton.setEnabled(true);
                                    current_state="new";
                                    sendMessageRequestButton.setText("Send Message");

                                    declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                    declineMessageRequestButton.setEnabled(false);
                            }

                        }
                    });
                }

            }
        });

    }

    private void sendChatRequest() {




        chatRequestDatabase.child(sendUserID).child(recivedUserIID)
                .child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    chatRequestDatabase.child(recivedUserIID).child(sendUserID)
                            .child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                sendMessageRequestButton.setEnabled(true);
                                current_state="request_sent";
                                sendMessageRequestButton.setText("Cancel Chat Request");
                            }

                        }
                    });
                }

            }
        });


    }

}
