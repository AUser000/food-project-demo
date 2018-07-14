package com.example.foodprojectdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        ///////////////////////////////////////////////////////////////////////////////////
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d("RegisterActivity", "onVerificationCompleted:" + phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
            }
        };
        ///////////////////////////////////////////////////////////////////////////////////
    }

    public void registerPhoneNumber(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
    }

    public void register(View view) {

        /*
        StringBuilder builder = new StringBuilder();
        builder.append(phoneCodeSpinner.getSelectedItem().toString());
        builder.append(phoneNumber.getText().toString());
        Log.d("RegisterActivity", "phoneNumber: " + builder.toString());
        registerPhoneNumber(builder.toString());
        */
    }
}
