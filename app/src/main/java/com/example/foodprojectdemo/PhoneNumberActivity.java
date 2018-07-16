package com.example.foodprojectdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class PhoneNumberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
    }

    public void verify(View view) {
        Spinner phoneCode = findViewById(R.id.phoneCode);
        EditText phoneNumber = findViewById(R.id.phoneNumber);
        StringBuilder builder = new StringBuilder();

        builder.append(phoneCode.getSelectedItem().toString());
        builder.append(phoneNumber.getText().toString());
        Intent intent = new Intent(PhoneNumberActivity.this, PhoneVerificationActivity.class);
        intent.putExtra(Intent.EXTRA_PHONE_NUMBER, builder.toString());
        startActivity(intent);
        finish();
    }
}
