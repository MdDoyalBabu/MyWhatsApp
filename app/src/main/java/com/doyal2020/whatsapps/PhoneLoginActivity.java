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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {


    private Button sendVerificationButton,verifyButton;
    private EditText phoneNumberEditext,verificationCodeEdittext;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    private FirebaseAuth mAuth;
    private String mVerificationId;

    private ProgressDialog mProgressDialog;

    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);


        mAuth=FirebaseAuth.getInstance();

        sendVerificationButton=findViewById(R.id.send_code_Button_Id);
        verifyButton=findViewById(R.id.verify_button_id);
        phoneNumberEditext=findViewById(R.id.phone_number_Edittext_Id);
        verificationCodeEdittext=findViewById(R.id.phone_verification_code_Edittext_d);
        mProgressDialog=new ProgressDialog(this);


        sendVerificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phoneNumber=phoneNumberEditext.getText().toString();

                if (TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(PhoneLoginActivity.this, "Please enter your valid number", Toast.LENGTH_SHORT).show();
                }
                else {

                    mProgressDialog.setTitle("Phone Verification");
                    mProgressDialog.setMessage("Please wait , while we are your authenticating your phone...");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            callbacks);        // OnVerificationStateChangedCallbacks

                }

            }

        });
        
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                sendVerificationButton.setVisibility(View.INVISIBLE);
                phoneNumberEditext.setVisibility(View.INVISIBLE);
                
                String verificationCode=verificationCodeEdittext.getText().toString();
                
                if (TextUtils.isEmpty(verificationCode)){
                    Toast.makeText(PhoneLoginActivity.this, "Please write verification code first......", Toast.LENGTH_SHORT).show();
                }
                else {
                    mProgressDialog.setTitle("Verification code");
                    mProgressDialog.setMessage("Please wait , while we verifying  verification code.. ...");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();


                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);

                }
                
            }
        });

        callbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                signInWithPhoneAuthCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                mProgressDialog.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Invalid number, Please correct number with your country code.....", Toast.LENGTH_SHORT).show();

                sendVerificationButton.setVisibility(View.VISIBLE);
                verifyButton.setVisibility(View.INVISIBLE);

                phoneNumberEditext.setVisibility(View.VISIBLE);
                verificationCodeEdittext.setVisibility(View.INVISIBLE);


            }
            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                mProgressDialog.dismiss();
                Toast.makeText(PhoneLoginActivity.this, "Code has been sent,Please check and verify", Toast.LENGTH_SHORT).show();

                sendVerificationButton.setVisibility(View.INVISIBLE);
                verifyButton.setVisibility(View.VISIBLE);

                phoneNumberEditext.setVisibility(View.INVISIBLE);
                verificationCodeEdittext.setVisibility(View.VISIBLE);

                // ...
            }
        };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            mProgressDialog.dismiss();

                            Toast.makeText(PhoneLoginActivity.this, "Wow , your are logging is Successful", Toast.LENGTH_SHORT).show();
                            sendUserToMainActivity();

                        } else {

                            String message=task.getException().toString();
                            Toast.makeText(PhoneLoginActivity.this, "Error"+message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void sendUserToMainActivity() {

        Intent intent=new Intent(PhoneLoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();

    }
}
