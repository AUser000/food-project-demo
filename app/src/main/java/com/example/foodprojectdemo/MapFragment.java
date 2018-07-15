package com.example.foodprojectdemo;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MapStyleOptions;
;import static android.content.ContentValues.TAG;

/**
 * Created by Dhanushka Dharmasena on 13/07/2018.
 */
public class MapFragment extends Fragment implements com.google.android.gms.maps.OnMapReadyCallback {
    GoogleMap map;
    FloatingActionButton fabM1, fabM2, fabM3;
    Animation fabOpen, fabClose, fabRotateForward, fabRotateBackWord;
    boolean fabIsOpen = false;
    public MapFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.map_fragment, container, false);
        //fabM1 = (FloatingActionButton)
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        fabM1 = (FloatingActionButton) view.findViewById(R.id.fabM1);
        fabM2 = (FloatingActionButton) view.findViewById(R.id.fabM2);
        fabM3 = (FloatingActionButton) view.findViewById(R.id.fabM3);

        fabOpen = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        fabRotateForward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_forword);
        fabRotateBackWord = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_backword);

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
                Toast.makeText(getActivity().getApplicationContext(), "location btn function", Toast.LENGTH_SHORT ).show();
            }
        });

        fabM3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                Toast.makeText(getActivity().getApplicationContext(), "filter btn function", Toast.LENGTH_SHORT).show();
            }
        });
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
}
