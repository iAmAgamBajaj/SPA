package com.bajaj.agam.spa;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private Button backButton;
    private Map<String,String> download;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        download = new HashMap<>();
        backButton = (Button)findViewById(R.id.backPot);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

        new getData().execute();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng;
        this.mMap = googleMap;
        for (String lat : download.keySet()){
            latLng = new LatLng(Double.valueOf(lat),Double.valueOf(download.get(lat).toString()));
            mMap.addMarker(new MarkerOptions().position(latLng).title("Pothole detected"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
        }
//        LatLng nearHostel = new LatLng(28.5463687,77.2731298);
//        googleMap.addMarker(new MarkerOptions().position(nearHostel).title("Pothole detected"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(nearHostel));
//
//        LatLng nearEntry = new LatLng(28.5462815,77.2727811);
//        googleMap.addMarker(new MarkerOptions().position(nearEntry).title("Pothole detected"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(nearEntry));
//
//        LatLng nearSeminar = new LatLng(28.5455087,77.2724994);
//        googleMap.addMarker(new MarkerOptions().position(nearSeminar).title("Pothole detected"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(nearSeminar));
//
//        LatLng nearFac = new LatLng(28.544553,77.2709324);
//        googleMap.addMarker(new MarkerOptions().position(nearFac).title("Pothole detected"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(nearFac));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.5462464,77.272704), 16.0f));
    }

    private class getData extends AsyncTask<String, Void, String>{
        String result,lat,longi;
        InputStream isr;
        @Override
        protected String doInBackground(String... strings) {
            result = "";
            isr = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://192.168.2.16/wardi/getPotholeData.php");
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                isr = entity.getContent();
                if(isCancelled())
                    return null;
            } catch (Exception e) {
                Log.e("log_tag", "Error in http connection " + e.toString());
                finish();

            }
            //convert response to string
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(isr, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                    if(isCancelled())
                        return null;
                }
                isr.close();

                result = sb.toString();
            } catch (Exception e) {
                Log.e("log_tag", "Error  converting result " + e.toString());
            }
            try {
                JSONObject jsonObject = new JSONObject(result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));
                Log.d("json",jsonObject.toString());
                JSONArray jArray = jsonObject.getJSONArray("result");
                for (int i = 0; i < jArray.length(); i++) {
                    if(isCancelled())
                        return null;
                    JSONObject json = jArray.getJSONObject(i);
                    lat = json.getString("lat");
                    longi = json.getString("long");
//                    name=name+"\n"+  json.getString("level");
                    download.put(lat,longi);
                }

            } catch (Exception e) {
                // TODO: handle exception
                Log.e("log_tag", "Error Parsing Data " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(MapsActivity.this);

        }
    }
}
