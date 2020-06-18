package com.doyal2020.whatsapps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.doyal2020.whatsapps.Adapter.MessagesAdapter;
import com.doyal2020.whatsapps.Holder.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {



    private String messageReceiverID,messageReceiverName,messageReciverImage,messageSenderID;
    private  TextView userName,userLastSeen;
    private EditText messageTextInPut;
    private ImageButton sendMessageBtn;
    private CircleImageView userProfileImage;
    private Toolbar mToolbar;
    private RecyclerView myRecyclerViewList;

    private final List<Messages> messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;

    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);





        messageReceiverID=getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("visit_user_name").toString();
        messageReciverImage=getIntent().getExtras().get("visit_user_image").toString();


        mDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp");
        mAuth=FirebaseAuth.getInstance();
        messageSenderID=mAuth.getCurrentUser().getUid();
        initializeControllers();



        userName.setText(messageReceiverName);
        Picasso.get().load(messageReciverImage).placeholder(R.drawable.profile_icon).into(userProfileImage);

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();

            }
        });


    }




    private void initializeControllers() {

        mToolbar=findViewById(R.id.chat_toolbar_id);
        setSupportActionBar(mToolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view=layoutInflater.inflate(R.layout.custom_user_layout,null);
        actionBar.setCustomView(view);



        userName=findViewById(R.id.custom_profile_name_id);
        userLastSeen=findViewById(R.id.custom_user_last_seen_id);
        userProfileImage=findViewById(R.id.custom_profile_image_id);

        messageTextInPut=findViewById(R.id.input_send_message_id);
        sendMessageBtn=findViewById(R.id.send_inputMessage_button_id);
        myRecyclerViewList=findViewById(R.id.private_send_message_users);

        messagesAdapter=new MessagesAdapter(messagesList);
        myRecyclerViewList.setAdapter(messagesAdapter);
        linearLayoutManager=new LinearLayoutManager(this);
        myRecyclerViewList.setLayoutManager(linearLayoutManager);



    }

    @Override
    protected void onStart() {

        super.onStart();

        mDatabase.child("Message").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        Messages messages=dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                      messagesAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    private void sendMessage() {

        final String messageText=messageTextInPut.getText().toString();

        if (TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "first message type here..", Toast.LENGTH_SHORT).show();
        }
        else {

            String sendMessageRef="Message/"+messageSenderID+"/"+messageReceiverID;
            String receivedMessageRef="Message/"+messageReceiverID+"/"+messageSenderID;

            DatabaseReference userMessageKeyRef=mDatabase.child("Message")
                    .child(messageSenderID).child(messageReceiverID).push();

            String messagePushID=userMessageKeyRef.getKey();

            Map messageTextBody=new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderID);

            Map messageTextDetails=new HashMap();
            messageTextDetails.put(sendMessageRef+"/"+messagePushID,messageTextBody);
            messageTextDetails.put(receivedMessageRef+"/"+messagePushID,messageTextBody);


            mDatabase.updateChildren(messageTextDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Message sent successful", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                messageTextInPut.setText("");
                }
            });


        }


    }
}
