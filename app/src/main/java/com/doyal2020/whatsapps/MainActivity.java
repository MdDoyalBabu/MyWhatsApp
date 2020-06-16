package com.doyal2020.whatsapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.doyal2020.whatsapps.Holder.TabAcesorAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


   private Toolbar mToolbar;
   private AppBarLayout mAppbarLayout;
   private ViewPager mViewPager;
    private TabLayout mTabLayout;

   private TabAcesorAdapter mTabAcesorAdapter;


   private FirebaseUser currentUser;
   private FirebaseAuth mAuth;
   private DatabaseReference mDatabase;

   private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar=findViewById(R.id.main_page_toolbar_id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("WhatApps");


        mDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp");
        mAuth=FirebaseAuth.getInstance();

        currentUser=mAuth.getCurrentUser();


        initializeFileds();

    }

    private void initializeFileds() {

        mAppbarLayout=findViewById(R.id.main_appBarLayout_Id);


        mViewPager=findViewById(R.id.main_ViewPager_Id);
        mTabAcesorAdapter=new TabAcesorAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabAcesorAdapter);

        mTabLayout=findViewById(R.id.mainTabs);
        mTabLayout.setupWithViewPager(mViewPager);

    }


    private void sendUserToLoginActivity() {
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_option_layout,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.main_Find_Friend_menu){

            sendUserToFrindFriendsActivity();
        }
        if (item.getItemId()==R.id.main_setting_menu){

            sendUserToSettingActivity();

        }
           if (item.getItemId()==R.id.main_CreateGroup_menu){

            registerNewCreateGroup();

        }

        if (item.getItemId()==R.id.main_logout_menu){

            mAuth.signOut();
            sendUserToLoginActivity();
            finish();

        }

        return super.onOptionsItemSelected(item);

    }



    private void registerNewCreateGroup() {

        AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);

        alertDialog.setTitle("Enter  Group Name");

        final  EditText groupNameFiled=new EditText(MainActivity.this);

        groupNameFiled.setHint("Cmt B21");
        alertDialog.setView(groupNameFiled);

        alertDialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                 String groupName=groupNameFiled.getText().toString();

                 if (TextUtils.isEmpty(groupName)){
                     Toast.makeText(MainActivity.this, "Please write a group name.........", Toast.LENGTH_SHORT).show();

                 }
                 else{

                     createGroup(groupName);
                 }

            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               dialog.cancel();

            }
        });
        alertDialog.show();




    }

    private void createGroup(final String groupName) {

        mDatabase.child("Groups").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, groupName+"is created successful", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void sendUserToSettingActivity() {
        Intent intent=new Intent(MainActivity.this,SettingActivity.class);
        startActivity(intent);

    }
    private void sendUserToFrindFriendsActivity() {
        Intent intent=new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser==null){
            sendUserToLoginActivity();
        }
        else {

            verifyUserExistence();

        }
    }

    private void verifyUserExistence() {

        currentUserId=mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.child("name").exists())){
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendUserToSettingActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
