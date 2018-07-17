package com.example.foodprojectdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodprojectdemo.sample.LocationData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

;

/**
 * Created by Dhanushka Dharmasena on 13/07/2018.
 */
public class MapFragment extends Fragment implements com.google.android.gms.maps.OnMapReadyCallback /* com.google.android.gms.location.LocationListener*/{
    GoogleMap map;
    FloatingActionButton fabM1, fabM2, fabM3;
    Animation fabOpen, fabClose, fabRotateForward, fabRotateBackWord;
    boolean fabIsOpen = false;
    LocationManager locationManager;
    String provider;
    Location location;
    MarkerOptions markerOptions;
    private FusedLocationProviderClient mFusedLocationClient;
    public final int REQUEST_PERMISSION_LOCATION = 101;
    private boolean permissionGranted = false;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference mChild;
    List<LocationData> locationDataList;
    TextView text;
    AlertDialog dialog;
    Marker marker;

    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
        markerOptions = new MarkerOptions();
        locationDataList = new ArrayList<>();
        text = (TextView) view.findViewById(R.id.textOnMap);

        fabM1 = (FloatingActionButton) view.findViewById(R.id.fabM1);
        fabM2 = (FloatingActionButton) view.findViewById(R.id.fabM2);
        fabM3 = (FloatingActionButton) view.findViewById(R.id.fabM3);

        fabOpen = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        fabRotateForward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_forword);
        fabRotateBackWord = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_backword);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mChild = databaseReference.child("locations");

        fabM1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });
        fabM2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                Toast.makeText(getActivity().getApplicationContext(), "location btn function", Toast.LENGTH_SHORT).show();

                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                provider = locationManager.getBestProvider(new Criteria(), false);

                checkPermission();
                if(permissionGranted) {
                    location = locationManager.getLastKnownLocation(provider);
                    if(location != null) {
                        map.addMarker(markerOptions.
                                position(new LatLng(location.getLatitude(), location.getLongitude())).
                                        icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_x))
                                );
                        map.animateCamera(CameraUpdateFactory.
                                newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
                    } else {
                        text.setText("null location");
                    }
                }
            }
        });
        fabM3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.filter_dialog_box, null);

                Button filterButton = (Button) mView.findViewById(R.id.filter_btn);
                filterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "processing", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        mChild.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot dataSnap2shot1: dataSnapshot.getChildren()) {
                                    LocationData locationData = dataSnap2shot1.getValue(LocationData.class);
                                    locationDataList.add(locationData);
                                }
                                for (LocationData locationdata: locationDataList) {
                                    double lat = locationdata.lat;
                                    double lng = locationdata.lng;

                                    map.addMarker(new MarkerOptions().position(new LatLng(lat, lng))
                                            );
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity().getApplicationContext(), " " + databaseError, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                aBuilder.setView(mView);
                dialog = aBuilder.create();
                dialog.show();


                mChild.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnap2shot1: dataSnapshot.getChildren()) {
                            LocationData locationData = dataSnap2shot1.getValue(LocationData.class);
                            locationDataList.add(locationData);
                        }
                        for (LocationData locationdata: locationDataList) {
                            double lat = locationdata.lat;
                            double lng = locationdata.lng;

                            map.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getActivity().getApplicationContext(), " " + databaseError, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private boolean checkPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_LOCATION
            );
            return false;
        }
        permissionGranted = true;
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity().getApplicationContext(), R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    private void animateFab() {
        if(fabIsOpen) {
            fabM1.startAnimation(fabRotateForward);
            fabM2.startAnimation(fabClose);
            fabM3.setAnimation(fabClose);
            fabM2.setClickable(false);
            fabM3.setClickable(false);
            fabIsOpen = false;
        } else {
            fabM1.startAnimation(fabRotateBackWord);
            fabM2.startAnimation(fabOpen);
            fabM3.setAnimation(fabOpen);
            fabM2.setClickable(true);
            fabM3.setClickable(true) ;
            fabIsOpen = true;
        }
    }

//    @Override
//    public void onLocationChanged(Location loc) {
//        location = loc;
//        if (marker != null){
//            marker.remove();
//        }
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions
//                .position(new LatLng(location.getLatitude(), location.getLongitude()))
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_orange));
//        marker = map.addMarker(markerOptions);
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
//    }
}
