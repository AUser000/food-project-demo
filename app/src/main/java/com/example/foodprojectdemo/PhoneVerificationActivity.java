package com.example.foodprojectdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import static com.example.foodprojectdemo.RegisterActivity.EXTRA_SMS_CODE;

public class PhoneVerificationActivity extends AppCompatActivity {

    private static final int VERIFICATION_CODE_LENGTH = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        updateUIOnVerificationCompleted(getIntent().getStringExtra(EXTRA_SMS_CODE));
    }

    // fill EditText fields with smsCode
    private void updateUIOnVerificationCompleted(String smsCode) {
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

    public void verify(View view) {
        finish();
    }
}
