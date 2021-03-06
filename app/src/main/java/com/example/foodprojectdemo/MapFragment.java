package com.example.foodprojectdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodprojectdemo.models.Category;
import com.example.foodprojectdemo.models.Inventory;
import com.example.foodprojectdemo.models.InventoryMetaData;
import com.example.foodprojectdemo.models.Item;
import com.example.foodprojectdemo.sample.LocationData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

;

/**
 * Created by Dhanushka Dharmasena on 13/07/2018.
 */
public class MapFragment extends Fragment implements com.google.android.gms.maps.OnMapReadyCallback{
    //map and markers
    GoogleMap map;
    Marker marker;
    MarkerOptions markerOptions;
    LocationManager locationManager;

    //View components
    FloatingActionButton fabM1, fabM2, fabM3;
    Animation fabOpen, fabClose, fabRotateForward, fabRotateBackWord;
    boolean fabIsOpen = false;
    TextView text;
    AlertDialog dialog;

    //location providers
    String provider;
    Location location;

    //permissions
    public final int REQUEST_PERMISSION_LOCATION = 101;
    private boolean permissionGranted = false;

    //database references
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference mChild;
    DatabaseReference childInventory;

    //lists
    List<Marker> markerList = new ArrayList<Marker>();
    List<InventoryMetaData> inventoryMetaDataList = new ArrayList<>();
    List<Category> catsArray =  new ArrayList<>();
    List<String> catsNameList = new ArrayList<>();
    List<Item> itmsArray =  new ArrayList<>();
    List<String> itmsNameList = new ArrayList<>();

    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);
        markerOptions = new MarkerOptions();
        text = (TextView) view.findViewById(R.id.textOnMap);

        fabM1 = (FloatingActionButton) view.findViewById(R.id.fabM1);
        fabM2 = (FloatingActionButton) view.findViewById(R.id.fabM2);
        fabM3 = (FloatingActionButton) view.findViewById(R.id.fabM3);
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
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(7.75, 80.7), 8));
        map.getUiSettings().setMapToolbarEnabled(false);
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity().getApplicationContext(), R.raw.style_json));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }



        fabOpen = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        fabRotateForward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_forword);
        fabRotateBackWord = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_backword);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        mChild = databaseReference.child("locations");
        childInventory = databaseReference.child("inventory");

        databaseReference.child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    Category categories = dataSnapshot1.getValue(Category.class);
                    catsArray.add(categories);
                }
                catsNameList.add("all");
                for (Category cat: catsArray) {
                    catsNameList.add(cat.name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity().getApplicationContext(), "database reading error! search under all", Toast.LENGTH_SHORT).show();
            }
        });


        databaseReference.child("items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                    Item item= dataSnapshot1.getValue(Item.class);
                    itmsArray.add(item);
                }

                itmsNameList.add("all");
                for (Item item: itmsArray) {
                    itmsNameList.add(item.name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity().getApplicationContext(), "database reading error! search under all", Toast.LENGTH_SHORT).show();
            }
        });



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
                if(permissionGranted || android.os.Build.VERSION.SDK_INT > 23) {
                    location = locationManager.getLastKnownLocation(provider);
                    if(location != null) {
                        map.addMarker(markerOptions.
                                position(new LatLng(location.getLatitude(), location.getLongitude())).
                                icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_x))
                        );
                        map.animateCamera(CameraUpdateFactory.
                                newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
                        text.setText("lat: "+location.getLatitude()+"\nlng: "+location.getLongitude());
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
                //item logic here
                Spinner cats = (Spinner) mView.findViewById(R.id.cat);

                ArrayAdapter<String> catsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, catsNameList);



                Spinner itms = (Spinner) mView.findViewById(R.id.itm);

                ArrayAdapter<String> itmsAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, itmsNameList);

                cats.setAdapter(catsAdapter);
                itms.setAdapter(itmsAdapter);

                Button filterButton = (Button) mView.findViewById(R.id.filter_btn);
                filterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "processing", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                        childInventory.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                markerList.clear();
                                for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                                    InventoryMetaData inventoryMetaData = new InventoryMetaData();
                                    inventoryMetaData.inventoryId = (String)dataSnapshot1.getKey();
                                    inventoryMetaData.itemId = (String)dataSnapshot1.child("itemId").getValue();
                                    inventoryMetaData.lat = (double)dataSnapshot1.child("lat").getValue();
                                    inventoryMetaData.lng = (double)dataSnapshot1.child("lng").getValue();
                                    inventoryMetaDataList.add(inventoryMetaData);
                                }

                                for (InventoryMetaData inventoryMetaData :inventoryMetaDataList) {
                                    double lat = inventoryMetaData.lat;
                                    double lng = inventoryMetaData.lng;
                                    String itemId = inventoryMetaData.itemId;
                                    String inventoryId = inventoryMetaData.inventoryId;
                                    marker = map.addMarker(new MarkerOptions()
                                            .position(new LatLng(lat, lng))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_y))
                                            .title("Item name : " + itemId)               // item
                                            .snippet("inventory id : " + inventoryId));    // inventory
                                    markerList.add(marker);
                                }
                                inventoryMetaDataList.clear();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });
                aBuilder.setView(mView);
                dialog = aBuilder.create();
                dialog.show();

            }
        });

        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent = new Intent(getActivity(),OrderActivity.class);
                intent.putExtra("InvId", marker.getSnippet().replace("inventory id : ",""));
                startActivity(intent);
            }
        });
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

// this code for location changes
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
