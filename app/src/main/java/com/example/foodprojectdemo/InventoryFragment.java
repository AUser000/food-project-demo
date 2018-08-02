package com.example.foodprojectdemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.foodprojectdemo.models.Inventory;
import com.example.foodprojectdemo.models.Item;
import com.example.foodprojectdemo.models.SpinnerItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InventoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InventoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InventoryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "InventoryFragment";

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private DatabaseReference mDatabase;

    private OnFragmentInteractionListener mListener;

    public InventoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InventoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InventoryFragment newInstance(String param1, String param2) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // firebase database reference
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // grant location permissions
        getLocationPermission();
        // construct a FusedLocationProviderClient
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        Button inventoryAddButton = view.findViewById(R.id.inventoryAddButton);
        inventoryAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // trader id
                final String traderId = FirebaseAuth.getInstance().getUid();

                // item id
                Spinner inventoryItemSpinner = view.findViewById(R.id.inventoryItemSpinner);
                SpinnerItem item = (SpinnerItem) inventoryItemSpinner.getSelectedItem();
                final String itemId = item.getKey();

                // price
                EditText inventoryPriceText = view.findViewById(R.id.inventoryPriceText);
                final Double price = Double.parseDouble(inventoryPriceText.getText().toString());

                // quantity
                EditText inventoryQuantityText = view.findViewById(R.id.inventoryQuantityText);
                final Long quantity = Long.parseLong(inventoryQuantityText.getText().toString());

                // started time
                final Long startedTime = System.currentTimeMillis() / 1000L;

                if (mLocationPermissionGranted) {
                    @SuppressLint("MissingPermission") Task locationResult =
                            mFusedLocationProviderClient.getLastLocation();
                    locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Location location = (Location) task.getResult();
                                final Double lat = location.getLatitude();
                                final Double lng = location.getLongitude();

                                Inventory inventory = new Inventory(
                                        traderId, itemId, price, quantity, startedTime, lat, lng);
                                // write into the database
                                mDatabase.child("inventory").push().setValue(inventory);
                            } else {
                                Log.d(TAG, "Current location is null");
                            }
                        }
                    });
                }
            }
        });

        //////////////////////////////////////////////////////////////////////
        ValueEventListener itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Item>> type =
                        new GenericTypeIndicator<HashMap<String, Item>>() {};
                Map<String, Item> items = dataSnapshot.getValue(type);
                updateUI(items, view);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabase.child("items").addValueEventListener(itemListener);
        //////////////////////////////////////////////////////////////////////
        return view;
    }

    private void updateUI(Map<String, Item> items, View view) {
        ArrayList<SpinnerItem> spinnerItemArrayList = new ArrayList<SpinnerItem>();
        for (Map.Entry<String, Item> entry : items.entrySet()) {
            spinnerItemArrayList.add(new SpinnerItem(entry.getKey(), entry.getValue().name));
        }
        ArrayAdapter<SpinnerItem> adapter = new ArrayAdapter<SpinnerItem>(
                view.getContext(), android.R.layout.simple_spinner_item, spinnerItemArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        Spinner spinner = view.findViewById(R.id.inventoryItemSpinner);
        spinner.setAdapter(adapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED)
                {
                    mLocationPermissionGranted = true;
                }
        }
    }
}
