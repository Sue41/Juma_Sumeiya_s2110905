package com.example.weatherapp;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.weatherapp.XmlParser.MapData;
import com.example.weatherapp.XmlParser.MapXmlPullParser;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.weatherapp.databinding.ActivityMapsBinding;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
Name: Sumeiya Juma
ID: s2110905
*/

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private String urlSource;
    private String result;
    private MapData mapData;
    private ArrayList<MapData> maplist ;
    private Map<String, String> cityIdMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        maplist = new ArrayList<>();

        cityIdMap.put("glasgow", "2648579");
        cityIdMap.put("london", "2643743");
        cityIdMap.put("newyork", "5128581");
        cityIdMap.put("oman", "287286");
        cityIdMap.put("mauritius", "934154");
        cityIdMap.put("bangladesh", "1185241");

        startProgress("10");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        back();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void back(){

        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            finish();
        });
    }

    public void startProgress(String cityId)
    {
//        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        if (InternetConnectivityChecker.isInternetAvailable(getApplicationContext())) {
            // Internet connection is available
            // Run network access on a separate thread;
            urlSource =  "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + cityId;
            new Thread(new MapsActivity.Task(urlSource)).start();
        } else {
            Toast.makeText(MapsActivity.this, "No internet Connection!!!", Toast.LENGTH_SHORT).show();
            // No internet connection
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 3000);
        }


    }
    private class Task implements Runnable
    {
        private String url;

        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {


            for (Map.Entry<String, String> entry : cityIdMap.entrySet()) {
                String cityName = entry.getKey();
                String cityCode = entry.getValue();

                url =  "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + cityCode;
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";
            result = "";

            Log.e("MyTag","in run");

            try
            {
                Log.e("MyTag","in try");
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                while ((inputLine = in.readLine()) != null)
                {
                    if(inputLine.contains("dc:") ){
                        result = result + inputLine.replaceAll("dc\\:","");
                        Log.e("MyTag",inputLine.replaceAll("dc\\:",""));
                    }else if(inputLine.contains("georss:point")){
                        result = result + inputLine.replaceAll("georss\\:","");
                        Log.e("MyTag",inputLine.replace("georss\\:",""));
                    }
                    else if(inputLine.contains("atom:")){
                        Log.e("MyTag",inputLine.replace("atom:",""));
                    }
                    else{
                        result = result + inputLine;
                        Log.e("MyTag",inputLine);}

                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }

            //Get rid of the first tag <?xml version="1.0" encoding="utf-8"?>
            int i = result.indexOf(">");
            result = result.substring(i+1);

            Log.e("MyTag - cleaned",result);

            result.replaceAll("dc\\:","");
            result.replaceAll("</rss>","");
            result.replaceAll("xmlns\\:","");
            result.replaceAll("atom\\:","");


            System.out.println(""+result);

            MapXmlPullParser parser =new MapXmlPullParser();
            InputStream inputStream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));

            mapData = parser.parse(inputStream,cityName);

                System.out.println(mapData.getCondition());
            maplist.add(mapData);
            }

            System.out.println(maplist.size());

            MapsActivity.this.runOnUiThread(new Runnable()
            {
            public void run() {
                LatLngBounds.Builder builder = new LatLngBounds.Builder();

                LatLng location = null;
                for (MapData data : maplist) {


                    System.out.println(data.getCondition());
                    location = new LatLng(data.getLatT(), data.getLongT());
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(location)
                            .title(data.getLocationName().toUpperCase())
                            .snippet(data.getCondition()));

                    // Show info window for each marker
                    marker.showInfoWindow();
                    // Include marker position in the LatLngBounds.Builder
                    builder.include(location);
                }
                LatLngBounds bounds = builder.build();

                int padding = 50;

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cameraUpdate);


            }
            });
        }

    }
}