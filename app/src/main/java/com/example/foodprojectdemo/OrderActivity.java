package com.example.foodprojectdemo;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrderActivity extends AppCompatActivity {

    TextView item, price, time, available, trader, contact;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference inventoryReference;
    DatabaseReference itemReference;
    DatabaseReference traderReference;
    String itemId = new String();
    String traderId = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        item = (TextView) findViewById(R.id.item);
        price = (TextView) findViewById(R.id.price);
        time = (TextView) findViewById(R.id.time);
        available = (TextView) findViewById(R.id.qut);
        trader = (TextView) findViewById(R.id.trader_name);
        contact = (TextView) findViewById(R.id.contact);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        String InvId = (String)getIntent().getExtras().getString("InvId");
        inventoryReference = databaseReference.child("inventory").child(InvId);

        //reading inventory data
        inventoryReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                price.setText(dataSnapshot.child("price").getValue(Long.class).toString());
                available.setText(dataSnapshot.child("quantity").getValue(Long.class).toString());
                itemId = dataSnapshot.child("itemId").getValue(String.class);
                item.setText(itemId);
                Toast.makeText(OrderActivity.this, ""+itemId, Toast.LENGTH_SHORT).show();
                traderId = dataSnapshot.child("traderId").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Database", "eRRoR");
            }
        });

        itemReference      = databaseReference.child("items").child(itemId);
        traderReference    = databaseReference.child("users").child(traderId);

        //reading item details
        itemReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                item.setText(dataSnapshot.child("name").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //reading trader data
        traderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trader.setText(dataSnapshot.child("firstName").getValue(String.class));
                contact.setText("no contact number");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
