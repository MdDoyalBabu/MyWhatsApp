package com.doyal2020.whatsapps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ScrollView mScrollView;
    private EditText messageInputEditText;
    private ImageButton sendMessageButton;
    private TextView displayTextMessage;

    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase,groupNameDatabase,groupMessageDatabase;

    private String currentGroupName1,currentUserID,currentUser1,currentDate,currentTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName1=getIntent().getExtras().get("GroupName").toString();
        mAuth=FirebaseAuth.getInstance();

        userDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("Users");
        groupNameDatabase=FirebaseDatabase.getInstance().getReference("WhatsApp").child("Groups").child(currentGroupName1);

        currentUserID=mAuth.getCurrentUser().getUid();



        Toast.makeText(GroupChatActivity.this, currentGroupName1, Toast.LENGTH_SHORT).show();



        initializeFields();

        getUserInFo();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveMessageInfoToDatabase();
                messageInputEditText.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

      groupNameDatabase.addChildEventListener(new ChildEventListener() {
          @Override
          public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
              if (dataSnapshot.exists()){
                  displayMessage(dataSnapshot);
              }
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


    private void initializeFields() {

        mToolbar=findViewById(R.id.group_chat_toolbar_id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName1);

        mScrollView=findViewById(R.id.my_group_scrollview_id);
        messageInputEditText=findViewById(R.id.inpurs_group_message_Id);
        sendMessageButton=findViewById(R.id.send_message_button_Id);
        displayTextMessage=findViewById(R.id.group_text_display_Id);



    }

    private void getUserInFo() {

        userDatabase.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    currentUser1=dataSnapshot.child("name").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveMessageInfoToDatabase() {

        String message=messageInputEditText.getText().toString();
        String messageKey=groupNameDatabase.push().getKey();

        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Please write message first.......", Toast.LENGTH_SHORT).show();
        }
        else {

            Calendar calForDate=Calendar.getInstance();
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM dd, yyyy");
            currentDate=simpleDateFormat.format(calForDate.getTime());

            Calendar calForTime=Calendar.getInstance();
            SimpleDateFormat simpletimeFormat=new SimpleDateFormat("hh: mm a");
            currentTime=simpletimeFormat.format(calForTime.getTime());

            HashMap<String,Object> groupMessageKey=new HashMap<>();
            groupNameDatabase.updateChildren(groupMessageKey);

             groupMessageDatabase=groupNameDatabase.child(messageKey);

            HashMap<String,Object> messageInfoMap=new HashMap<>();


            messageInfoMap.put("message",message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time",currentTime);
            messageInfoMap.put("name",currentUser1);

            groupMessageDatabase.updateChildren(messageInfoMap);

        }

    }



    private void displayMessage(DataSnapshot dataSnapshot) {


        Iterator iterator=dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()){

            String chatDate=(String) ((DataSnapshot)iterator.next()).getValue().toString();
            String chatMessage=(String) ((DataSnapshot)iterator.next()).getValue().toString();
            String chatUserName=(String) ((DataSnapshot)iterator.next()).getValue().toString();
            String chatTime=(String) ((DataSnapshot)iterator.next()).getValue().toString();

            displayTextMessage.append(chatUserName+":\n"+chatMessage +" \n"+chatDate+"     " +chatTime+"\n\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);

        }


    }
}
