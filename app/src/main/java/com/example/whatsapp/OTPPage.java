package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.executor.TaskExecutor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class OTPPage extends AppCompatActivity {
    Pinview pinview;
    ProgressDialog dialog;
    Button next;
    String verificationId;
    FirebaseAuth mAuth;
    //FirebaseAuthSettings mauthSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otppage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pinview = findViewById(R.id.pin_id);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Verifying");
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);
        next = findViewById(R.id.next_id);
        mAuth = FirebaseAuth.getInstance();
        //  mauthSettings=mAuth.getFirebaseAuthSettings();

        final String phoneNumber = getIntent().getExtras().getString(MainActivity.PHONE_NUMBER);

        VerifyCode(phoneNumber);

        next.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        /*InputMethodManager methodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        methodManager.hideSoftInputFromWindow(view.getWindowToken(),0);*/

                        String code = pinview.getValue().trim();
                        if (code.isEmpty() || code.length() < 6) {
                            Toast.makeText(OTPPage.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.show();
                        signIn(code);
                    }
                });
    }

    @Override
    protected void onResume() {
        boolean networkAvailable = connectivity.isNetworkAvailable(OTPPage.this);
        if (!networkAvailable) {
            Toast.makeText(this, "Network not Available", Toast.LENGTH_SHORT).show();
        }
        super.onResume();

    }

    private void signIn(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Intent intent = new Intent(OTPPage.this, profile_settings.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    return;
                }
                Log.d(MainActivity.TAG, "SignIn Function: called in OTPPage");
                Log.d(MainActivity.TAG, "Error: " + task.getException().getMessage());
            }
        });

    }


    private void VerifyCode(String phoneNumber) {
        dialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                callback
        );
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            Log.d(MainActivity.TAG, "onCodeSent: " + s);
            verificationId = s;
            super.onCodeSent(s, forceResendingToken);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            dialog.cancel();
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                signIn(code);
                pinview.setValue(code);
            } else {
                dialog.cancel();
                Log.d(MainActivity.TAG, "onVerificationCompleted: failed");
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            dialog.cancel();
            Log.d(MainActivity.TAG, "onVerificationFailed: called in OTPPage");
            Log.d(MainActivity.TAG, "Error :" + e.getMessage());
            Toast.makeText(OTPPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

}
