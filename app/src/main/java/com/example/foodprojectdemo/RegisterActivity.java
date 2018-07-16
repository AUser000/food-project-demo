package com.example.foodprojectdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.foodprojectdemo.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }


    public void register(String type) {
        User user = new User(
                ((EditText) findViewById(R.id.firstNameText)).getText().toString(),
                ((EditText) findViewById(R.id.lastNameText)).getText().toString(),
                ((EditText) findViewById(R.id.email)).getText().toString(),
                type
        );
        String uId = getIntent().getStringExtra(Intent.EXTRA_UID);
        mDatabaseRef.child("users").child(uId).setValue(user);
    }


    public void registerCustomer(View view) {
        register("customer");
        finish();
    }

    public void registerTrader(View view) {
        register("trader");
        finish();
    }
}
