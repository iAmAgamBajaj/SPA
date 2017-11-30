package com.bajaj.agam.spa;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class PotholeActivity extends AppCompatActivity implements OnMapReadyCallback {

    Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pothole);
        backButton = (Button)findViewById(R.id.backPot);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

//        if(!getLocation()){
//            enableButton();
//        }
//
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        getLocation();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GoogleMap mMap=googleMap;
        LatLng nearHostel = new LatLng(28.5463687,77.2731298);
        googleMap.addMarker(new MarkerOptions().position(nearHostel).title("Pothole detected"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(nearHostel));

        LatLng nearEntry = new LatLng(28.5462815,77.2727811);
        googleMap.addMarker(new MarkerOptions().position(nearEntry).title("Pothole detected"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(nearEntry));

        LatLng nearSeminar = new LatLng(28.5455087,77.2724994);
        googleMap.addMarker(new MarkerOptions().position(nearSeminar).title("Pothole detected"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(nearSeminar));

        LatLng nearFac = new LatLng(28.544553,77.2709324);
        googleMap.addMarker(new MarkerOptions().position(nearFac).title("Pothole detected"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(nearFac));
//
//        MapStyleOptions styleOptions=MapStyleOptions.loadRawResourceStyle(this,R.raw.map_style);
//        mMap.setMapStyle(styleOptions);

    }
}
