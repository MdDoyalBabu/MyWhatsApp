package com.doyal2020.whatsapps;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {



    private String messageReceiverID,messageReceiverName,messageReciverImage;
    private  TextView userName,userLastSeen;
    private CircleImageView userProfileImage;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);





        messageReceiverID=getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName=getIntent().getExtras().get("visit_user_name").toString();
        messageReciverImage=getIntent().getExtras().get("visit_user_image").toString();


        initializeControllers();



        userName.setText(messageReceiverName);
        Picasso.get().load(messageReciverImage).placeholder(R.drawable.profile_icon).into(userProfileImage);


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



    }
}
