package com.example.foodprojectdemo.helper;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;

import java.util.concurrent.Executor;

public class SignInHelper {

    private static final String TAG = "RegisterActivity";

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void signInWithPhoneAuthCredentials(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                        } else {
                            Log.w(TAG, "signInWithCredential:failure");
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // the verification code entered is invalid
                            }
                        }
                    }
                });
    }
}
