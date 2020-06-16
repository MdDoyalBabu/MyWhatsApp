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

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser currentUser;

    private Button loginButton,phoneButton;
    private EditText userEmail,userPassword;
    private TextView forgetPassword,needNewAccount;
    private ProgressDialog mProgressDialog;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        currentUser=mAuth.getCurrentUser();


        initializeFileds();

        needNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             sendUserToRegisterActivity();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowuserLogin();
            }
        });
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(LoginActivity.this,PhoneLoginActivity.class);
                startActivity(intent);

            }
        });

    }

    private void allowuserLogin() {

        String email=userEmail.getText().toString();
        String password=userPassword.getText().toString();


        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please,Enter your email address", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please,Enter any password", Toast.LENGTH_SHORT).show();
        }
        else {

            mProgressDialog.setTitle("Sign In.........");
            mProgressDialog.setMessage("Please wait............");
            mProgressDialog.setCanceledOnTouchOutside(true);
            mProgressDialog.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                mProgressDialog.dismiss();
                                sendUserToMainActivity();
                                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                mProgressDialog.dismiss();
                                String message=task.getException().toString();
                                Toast.makeText(LoginActivity.this, "Please create your account", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }

    }


    private void initializeFileds() {

        loginButton=findViewById(R.id.login_button_id);
        phoneButton=findViewById(R.id.login_Phone_button_id);

        userEmail=findViewById(R.id.login_email_id);
        userPassword=findViewById(R.id.login_password_id);

        forgetPassword=findViewById(R.id.login_forget_password_Link);
        needNewAccount=findViewById(R.id.login_need_an_account_id);

        mProgressDialog=new ProgressDialog(this);

    }



    private void sendUserToMainActivity() {
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }

    private void sendUserToRegisterActivity() {
        Intent loginIntent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(loginIntent);
    }
}
