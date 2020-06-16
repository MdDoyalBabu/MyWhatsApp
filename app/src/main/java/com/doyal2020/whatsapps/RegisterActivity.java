package com.doyal2020.whatsapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {


    private Button createAccountButton;
    private EditText userEmail,userPassword;
    private TextView alreadyAccont;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance().getReference("WhatsApp");





        initializeFileds();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userCreateAccount();
            }
        });

        alreadyAccont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendUserToLoginActivity();

            }
        });

    }

    private void userCreateAccount() {

        String email=userEmail.getText().toString();
        String password=userPassword.getText().toString();


        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please,Enter your email address", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please,Enter any password", Toast.LENGTH_SHORT).show();
        }
        else {

            mProgressDialog.setTitle("Create a  new Account...");
            mProgressDialog.setMessage("Please, while we are create account for you............");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.show();

            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){

                                String currentUserID=mAuth.getCurrentUser().getUid();

                                mDatabase.child("Users").child(currentUserID).setValue("");

                                mProgressDialog.dismiss();
                                sendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this, "Successful create your account", Toast.LENGTH_SHORT).show();
                            }
                            else {

                                String message=task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error"+message, Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            }
                        }
                    });

        }

    }

    private void sendUserToMainActivity() {
        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    private void initializeFileds() {

        createAccountButton=findViewById(R.id.register_create_button_id);
        userEmail=findViewById(R.id.register_email_id);
        userPassword=findViewById(R.id.register_password_id);
        alreadyAccont=findViewById(R.id.register_already_have_an_account_id);

        mProgressDialog=new ProgressDialog(this);

    }


    private void sendUserToLoginActivity() {

        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);

    }


}
