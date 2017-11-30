package com.bajaj.agam.spa;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class PollutionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button backButton;
    private LineChart chart;
    private String result;
    private InputStream isr;
    private LimitLine upperLimit;
    private YAxis leftAxis;
    private ArrayList<Entry> entries;
    private Map<String,String> download;
    private getData server_pull;
    private String[] times=new String[50];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pollution);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_pollution);
        mapFragment.getMapAsync(this);

        backButton = (Button)findViewById(R.id.backPol);
//        refreshButton = (Button)findViewById(R.id.refreshPol);
        chart = (LineChart) findViewById(R.id.linechart);
        entries = new ArrayList<>();
        server_pull = new getData();
        download = new HashMap<>();

//        refreshButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    server_pull.cancel(true);
//                    server_pull.execute();
//                }catch (IllegalStateException e){
//                    Log.e("In executing Async task", "task already running");
//                }
//            }
//        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

        server_pull.execute("");
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.5462464,77.272704), 16.0f));
    }

    private void chartInit(){
        //call inside async task
        chart.setDragEnabled(true);
        chart.setScaleEnabled(false);
        chart.setPinchZoom(true);

        upperLimit=new LimitLine(280f, "Danger");
        upperLimit.setLineWidth(2f);
        upperLimit.enableDashedLine(10f,10f,0f);
        upperLimit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        upperLimit.setTextSize(15f);

        leftAxis=chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(upperLimit);
        leftAxis.enableGridDashedLine(10f,10f,0);
        leftAxis.setDrawLimitLinesBehindData(true);
    }

    private class getData extends AsyncTask<String, Void, String> {
        String d_level,d_time;

        @Override
        protected String doInBackground(String... params) {
            result = "";
            isr = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://192.168.2.16/wardi/getPollutionData.php");
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

                result = sb.toString();//.substring(result.indexOf("{"), result.lastIndexOf("}") + 1);
//                Log.d("result",result.substring(result.indexOf("{"), result.lastIndexOf("}") + 1));
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
                    d_level = json.getString("level");
                    d_time = json.getString("lat");
//                    name=name+"\n"+  json.getString("level");
                    download.put(d_time,d_level);
                }

            } catch (Exception e) {
                // TODO: handle exception
                Log.e("log_tag", "Error Parsing Data " + e.toString());
            }
//            Log.d("TESTING",name);
            return d_level;
        }

        @Override
        protected void onPostExecute(String result) {
            int index=0;
            for( String time : download.keySet() ){
                times[index]=time;
                entries.add(new BarEntry(index,Float.valueOf(download.get(time))));
                index++;
            }
            Log.d("INDEX",String.valueOf(entries.toString()));

            LineDataSet lineDataSet=new LineDataSet(entries, "Data Set 1");
            lineDataSet.setFillAlpha(110);
            lineDataSet.setColor(Color.RED);
            lineDataSet.setLineWidth(3f);
            ArrayList<ILineDataSet> dataSets=new ArrayList<>();
            dataSets.add(lineDataSet);
            LineData data= new LineData(dataSets);
            chart.setData(data);

            XAxis xAxis=chart.getXAxis();
            xAxis.setValueFormatter(new MyAxis(times));
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        }

        @Override
        protected void onPreExecute() {
            chartInit();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public class MyAxis implements IAxisValueFormatter
    {
        private String[] mval;
        public MyAxis(String[] values)

        {
            this.mval=values;
        }
        @Override
        public String getFormattedValue(float value, AxisBase axisBase)
        {
            return mval[(int)value];

        }
    }
}
