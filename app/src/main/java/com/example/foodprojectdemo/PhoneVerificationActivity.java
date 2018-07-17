package com.example.foodprojectdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class PhoneVerificationActivity extends AppCompatActivity {

    private static final int VERIFICATION_CODE_LENGTH = 6;
    private static final String TAG = "PhoneVerification";

    private String mUId;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean isReAuthentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();

        ///////////////////////////////////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);

                // 1 => instant verification or
                // 2 => auto-retrieval verification
                signInWithPhoneAuthCredentials(credential);
                updateUIOnVerificationCompleted(credential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // the SMS quota for the project has been exceeded
                }
            }
        };
        ///////////////////////////////////////////////////////////////////////////////////
        String phoneNumber = getIntent().getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        registerPhoneNumber(phoneNumber);
    }

    // fill EditText fields with smsCode
    private void updateUIOnVerificationCompleted(String smsCode) {
        if (smsCode == null) {
            // assume this as an instant verification
            // todo: notify user the verification type
            smsCode = "000000";
        }
        EditText[] texts = new EditText[VERIFICATION_CODE_LENGTH];

        texts[0] = findViewById(R.id.smsCode1);
        texts[1] = findViewById(R.id.smsCode2);
        texts[2] = findViewById(R.id.smsCode3);
        texts[3] = findViewById(R.id.smsCode4);
        texts[4] = findViewById(R.id.smsCode5);
        texts[5] = findViewById(R.id.smsCode6);
        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            texts[i].setText(String.valueOf(smsCode.charAt(i)), TextView.BufferType.EDITABLE);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void checkForReAuthentications() {
        DatabaseReference userRef = mDatabaseRef.child("users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isReAuthentication = dataSnapshot.child(mUId).exists();
                // enable next button
                Button button = findViewById(R.id.verifyButton);
                button.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");

                            mUId = task.getResult().getUser().getUid();
                            checkForReAuthentications();
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

    public void registerPhoneNumber(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks
        );
    }

    public void next(View view) {
        if (isReAuthentication) {
            finish();
        } else {
            Intent intent = new Intent(PhoneVerificationActivity.this, RegisterActivity.class);
            intent.putExtra(Intent.EXTRA_UID, mUId);
            startActivity(intent);
            finish();
        }
    }
}
