package com.example.foodprojectdemo;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public class RegisterActivity extends AppCompatActivity {

    static final String EXTRA_USER_DETAILS = "com.example.foodprojectdemo.EXTRA_USER_DETAILS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void register(String type) {
        Bundle bundle = new Bundle();
        StringBuilder builder = new StringBuilder();
        Intent intent = new Intent(RegisterActivity.this, PhoneVerificationActivity.class);

        // build phone number combining phone code
        builder.append(
                ((Spinner) findViewById(R.id.phoneCodeSpinner)).getSelectedItem().toString());
        builder.append(
                ((EditText) findViewById(R.id.phoneNumer)).getText().toString());

        // bundle up everything
        bundle.putString("FIRST_NAME",
                ((EditText) findViewById(R.id.firstNameText)).getText().toString());
        bundle.putString("LAST_NAME",
                ((EditText) findViewById(R.id.lastNameText)).getText().toString());
        bundle.putString("EMAIL",
                ((EditText) findViewById(R.id.email)).getText().toString());
        bundle.putString("PHONE_NUMBER", builder.toString());
        bundle.putString("TYPE", type);

        // start new activity
        intent.putExtra(EXTRA_USER_DETAILS, bundle);
        startActivity(intent);
    }

    public void registerCustomer(View view) {
        register("customer");
    }

    public void registerTrader(View view) {
        register("trader");
    }
}
