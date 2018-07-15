package com.example.foodprojectdemo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.foodprojectdemo.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import static com.example.foodprojectdemo.RegisterActivity.EXTRA_USER_DETAILS;

public class PhoneVerificationActivity extends AppCompatActivity {

    private static final int VERIFICATION_CODE_LENGTH = 6;
    private static final String TAG = "PhoneVerification";

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;
    private FirebaseAuth mAuth;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

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
        String phoneNumber = getIntent()
                .getBundleExtra(EXTRA_USER_DETAILS).getString("PHONE_NUMBER");
        registerPhoneNumber(phoneNumber);
    }

    // write new user to database
    private void writeNewUser(String userId) {
        Bundle bundle = getIntent().getBundleExtra(EXTRA_USER_DETAILS);
        User user = new User(
            bundle.getString("FIRST_NAME"), bundle.getString("LAST_NAME"),
                bundle.getString("EMAIL"), bundle.getString("TYPE"));

        mDatabaseRef.child("users").child(userId).setValue(user);
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
        // enable verification button
        Button button = findViewById(R.id.verifyButton);
        button.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            writeNewUser(user.getUid());
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

    public void verify(View view) {
        finish();
    }
}
