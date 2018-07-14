package com.example.foodprojectdemo;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    static final String EXTRA_SMS_CODE = "com.example.foodprojectdemo.EXTRA_SMS_CODE";

    private FirebaseAuth mAuth;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ///////////////////////////////////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);

                // no instant verification
                String smsCode = phoneAuthCredential.getSmsCode();
                if (smsCode != null) {
                    Intent intent = new Intent(RegisterActivity.this,
                            PhoneVerificationActivity.class);
                    intent.putExtra(EXTRA_SMS_CODE, smsCode);
                    startActivity(intent);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // the sms quota for the project has been expired
                }
            }
        };
        ///////////////////////////////////////////////////////////////////////////////////
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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

    public void register(View view) {
        StringBuilder builder = new StringBuilder();

        builder.append(
                ((Spinner) findViewById(R.id.phoneCodeSpinner)).getSelectedItem().toString());
        builder.append(
                ((EditText) findViewById(R.id.phoneNumer)).getText().toString());
        registerPhoneNumber(builder.toString());
    }
}
