package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.weatherapp.XmlParser.WeatherData;
import com.example.weatherapp.XmlParser.XmlPullParserHandler;
import com.example.weatherapp.databinding.ActivityMain2Binding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.jakewharton.threetenabp.AndroidThreeTen;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.TextStyle;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.Map;

/*
Name: Sumeiya Juma
ID: s2110905
*/
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private AppBarConfiguration mAppBarConfiguration;
    private GoogleMap mMap;
    private ActivityMain2Binding binding;
    HashMap<String, String> cityIdMap = new HashMap<>();

    private String cityID;

    private SearchView searchView;
    private String result;
    private String Place;
    private String url1="";
    public static List<WeatherData> weatherData;
    private TextView place;
    private TextView day;
    private TextView temparature;
    private TextView condition;
    private TextView minTemp;
    private TextView maxTemp;
    private TextView dayname;
    private  TextView daydate;

    private TextView wind;
    private ConstraintLayout main;
    private TextView pressure;
    private TextView humidity;
    private TextView sunset;
    private ImageView next;
    private ImageView back;
    private TextView sunrise;
    private TextView condition1;
    private CalendarView calendarView;
    private LottieAnimationView animation;

    private FrameLayout mapContainer;
    private boolean mapTouched = false;

    private int index = 0;
    private String urlSource="https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/2643123";

    private static final Map<String, ZoneId> locationTimeZones = new HashMap<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());



        startProgress("2648579");
        Place = "glasgow";



        //A List of city and city codes
        cityIdMap.put("glasgow", "2648579");
        cityIdMap.put("london", "2643743");
        cityIdMap.put("newyork", "5128581");
        cityIdMap.put("oman", "287286");
        cityIdMap.put("mauritius", "934154");
        cityIdMap.put("bangladesh", "1185241");

       //List of all zone id
        locationTimeZones.put("london", ZoneId.of("Europe/London"));
        locationTimeZones.put("oman", ZoneId.of("Asia/Muscat"));
        locationTimeZones.put("bangladesh", ZoneId.of("Asia/Dhaka"));
        locationTimeZones.put("mauritius", ZoneId.of("Indian/Mauritius"));
        locationTimeZones.put("glasgow", ZoneId.of("Europe/London")); // Assuming same as London
        locationTimeZones.put("new york", ZoneId.of("America/New_York"));

        //initialise all views
        searchView = findViewById(R.id.searchView);
        place = findViewById(R.id.place);
        day = findViewById(R.id.day);
        temparature = findViewById(R.id.temparature);
        condition = findViewById(R.id.condition);
        condition1 = findViewById(R.id.condition1);
        minTemp = findViewById(R.id.minTemp);
        maxTemp = findViewById(R.id.maxTemp);
        dayname = findViewById(R.id.dayname);
        daydate = findViewById(R.id.daydate);
        sunrise = findViewById(R.id.sunrise);
        sunset = findViewById(R.id.sunset);
        wind = findViewById(R.id.wind);
        humidity = findViewById(R.id.humidity);
        pressure = findViewById(R.id.pressure);
        animation = findViewById(R.id.lottieAnimationView);
        main = findViewById(R.id.main);
        next = findViewById(R.id.next);
        calendarView = findViewById(R.id.calendarView);

        back = findViewById(R.id.back);
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        updateData();
        calenderSetUp();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Set OnClickListener for the next button
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Increment index and update data
                index++;
                data(index);
                updateData();
            }
        });

       // Set OnClickListener for the back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Decrement index and update data
                index--;
                data(index);
                updateData();
            }
        });
        
        // Setting up click listener for navigation menu items
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Get the ID of the clicked menu item
                int itemId = item.getItemId();
                drawer.closeDrawer(GravityCompat.START);
                // Compare the IDs directly without using a switch statement
                if (itemId == R.id.glasgow) {
                    // Handle Glasgow click
                    Place = "glasgow";
                    startProgress(cityIdMap.get("glasgow"));
                    Toast.makeText(MainActivity.this, "Glasgow ", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.oman) {
                    // Handle Oman click
                    Place = "oman";
                    startProgress(cityIdMap.get("oman"));
                    Toast.makeText(MainActivity.this, "Oman", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.london) {
                    // Handle London click
                    Place = "london";
                    startProgress(cityIdMap.get("london"));
                    Toast.makeText(MainActivity.this, "London ", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.newyork) {
                    // Handle New York click
                    Place = "New york";
                    startProgress(cityIdMap.get("newyork"));
                    Toast.makeText(MainActivity.this, "New York", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.mauritius) {
                    // Handle Mauritius click
                    Place = "mauritius";
                    startProgress(cityIdMap.get("mauritius"));
                    Toast.makeText(MainActivity.this, "Mauritius", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.bangladesh) {
                    // Handle Bangladesh click
                    Place = "bangladesh";
                    startProgress(cityIdMap.get("bangladesh"));
                    Toast.makeText(MainActivity.this, "Bangladesh", Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search when the user submits the query
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Perform search as the user types (optional)
                // You can implement live search functionality here
                return false;
            }
        });

        showMaps();
    }

    private void showMaps(){
        findViewById(R.id.maps).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MapsActivity.class)));
    }

    private void calenderSetUp(){

        // Disable dates outside the range of today, tomorrow, and the day after tomorrow
        Calendar minDate = Calendar.getInstance();
        minDate.setTime(new Date());
        calendarView.setMinDate(minDate.getTimeInMillis());

        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, 3); // Adding 2 days to get the day after tomorrow
        calendarView.setMaxDate(maxDate.getTimeInMillis());

        // Set a listener to handle date selection
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Get the selected date as a Calendar object
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                // Get the current date
                Calendar currentDate = Calendar.getInstance();

                // Calculate the difference in days between the selected date and the current date
                long differenceInMillis = selectedDate.getTimeInMillis() - currentDate.getTimeInMillis();
                int daysDifference = (int) (differenceInMillis / (1000 * 60 * 60 * 24));

                // Check if the selected date is within the allowed range (0 to 2 days from the current date)
                if (daysDifference == 0) {
                    index = 0;  // Current date
                } else if (daysDifference == 1) {
                    index = 1;  // Next two days after today
                }else if ( daysDifference == 2){
                    index = 2;
                } else {
                    // Do nothing for dates other than today and the next two days
                    return;
                }

                data(index);
                findViewById(R.id.scroll).scrollTo(0,0);
                updateData();
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // If the touch event is not inside the map container, let it pass through to the parent
        if (!mapTouched) {
            return super.dispatchTouchEvent(ev);
        }
        // Otherwise, consume the touch event to prevent ScrollView from scrolling
        return true;
    }
    private void performSearch(String query) {
        // Check if the entered query is a valid city name
        String cityId = cityIdMap.get(query.toLowerCase());
        Place = query;
        if (cityId != null) {
            Place = query;
            startProgress(cityIdMap.get(query));
        } else {
            // City not found
            Toast.makeText(this, "City not found", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateData() {


        // Show/hide back button based on index
        if (index <= 0) {
            back.setVisibility(View.GONE);
        } else {
            back.setVisibility(View.VISIBLE);
        }

        // Show/hide next button based on index
        if (index >= 2) {
            next.setVisibility(View.GONE);
        } else {
            next.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void startProgress(String cityId)
    {
        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        if (InternetConnectivityChecker.isInternetAvailable(getApplicationContext())) {
            // Internet connection is available
            // Run network access on a separate thread;
            urlSource =  "https://weather-broker-cdn.api.bbci.co.uk/en/forecast/rss/3day/" + cityId;
            new Thread(new MainActivity.Task(urlSource)).start();
        } else {
            Toast.makeText(MainActivity.this, "No internet Connection!!!", Toast.LENGTH_SHORT).show();
            // No internet connection
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 3000);
        }


    } //

    public static LocalDate getForecastDateForLocation(int index, String location) {
        ZoneId locationTimeZone = locationTimeZones.get(location);
        if (locationTimeZone == null) {
            throw new IllegalArgumentException("Unknown location: " + location);
        }

        // Get the current date in the location's time zone
        LocalDate currentDate = LocalDate.now(locationTimeZone);

        // Calculate the forecast date based on the index
        LocalDate forecastDate = currentDate.plusDays(index);

        return forecastDate;
    }

    // Need separate thread to access the internet resource over network
    // Other neater solutions should be adopted in later iterations.
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

            XmlPullParserHandler parser =new XmlPullParserHandler();
            InputStream inputStream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8));

            weatherData = parser.parse(inputStream);


            MainActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                 data(index);
                }
            });
        }

    }

    public void data(int index) {


        if( weatherData.size() > 0){
            Log.d("UI thread", "I am the UI thread");
            LocalDate currentDate =  getForecastDateForLocation(index,Place.toLowerCase());
            String dayOfWeek = currentDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            // Format to get the date in the format: date, month year
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM uuuu", Locale.ENGLISH);
            String formattedDate = currentDate.format(dateFormatter);


            String mnTemp = weatherData.get(index).getMinTemp();
            String mxTemp = weatherData.get(index).getMaxTemp();

            int intmnTemp =  0;
            int intmxTemp = 0;
            int temp = 0;


            if(weatherData.get(0).getMinTemp() == null){
                mnTemp = "";
            }else{
                intmnTemp = Integer.parseInt(mnTemp.replaceAll("[^0-9]", ""));
            }

            if(weatherData.get(0).getMaxTemp() == null){
                mxTemp = "";
            }else{
                intmxTemp = Integer.parseInt(mxTemp.replaceAll("[^0-9]", ""));
            }


            if(intmxTemp == 0 || intmnTemp == 0){
                temp = intmnTemp + intmxTemp;
            }else{
                temp = (intmnTemp + intmxTemp)/2;
            }



            place.setText(Place.substring(0, 1).toUpperCase() + Place.substring(1));
            day.setText(weatherData.get(index).getDay());
            temparature.setText(""+temp+"°C");
            maxTemp.setText("Max: "+weatherData.get(index).getMaxTemp());
            minTemp.setText("Min: "+weatherData.get(index).getMinTemp());
            condition.setText(weatherData.get(index).getCondition());
            sunset.setText(weatherData.get(index).getSunset());
            sunrise.setText(weatherData.get(index).getSunrise());
            pressure.setText(weatherData.get(index).getPressure());
            wind.setText(weatherData.get(index).getWind());
            condition1.setText(weatherData.get(index).getCondition());
            humidity.setText(weatherData.get(index).getHumidity());
            dayname.setText(dayOfWeek);
            daydate.setText(formattedDate);


            if(weatherData.get(index).getCondition().toLowerCase().contains("light cloudy") || weatherData.get(index).getCondition().toLowerCase().contains("partly cloudy")){
                animation.setAnimation("ligth_cloud.json");
                animation.playAnimation();
                animation.loop(true);
                main.setBackgroundResource(R.drawable.colud_background);
            }else if(weatherData.get(index).getCondition().toLowerCase().contains("clear sky")){
                if(!weatherData.get(index).getDay().equalsIgnoreCase("Tonight")) {
                    animation.setAnimation("sun.json");
                    animation.playAnimation();
                    animation.loop(true);
                    main.setBackgroundResource(R.drawable.sunny_background);}else{
                    animation.setAnimation("night1.json");
                    animation.playAnimation();
                    animation.loop(true);
                    main.setBackgroundResource(R.drawable.spalsh_screen1);
                }


            }else if(weatherData.get(index).getCondition().toLowerCase().contains("cloud")){
                animation.setAnimation("cloud.json");
                animation.playAnimation();
                animation.loop(true);
                main.setBackgroundResource(R.drawable.colud_background);
            }else if(weatherData.get(index).getCondition().toLowerCase().contains("sun")){
                animation.setAnimation("sun.json");
                animation.playAnimation();
                animation.loop(true);
                main.setBackgroundResource(R.drawable.sunny_background);
            }else if(weatherData.get(index).getCondition().toLowerCase().contains("wind")){
                animation.setAnimation("wind.json");
                animation.playAnimation();
                animation.loop(true);
                main.setBackgroundResource(R.drawable.wind_background);
            } else if(weatherData.get(index).getCondition().toLowerCase().contains("snow")){
                animation.setAnimation("snow.json");
                animation.playAnimation();
                animation.loop(true);
                main.setBackgroundResource(R.drawable.snow_background1);
            } else if (weatherData.get(index).getCondition().toLowerCase().contains("showers") || weatherData.get(index).getCondition().toLowerCase().contains("rain")) {
                animation.setAnimation("showers.json");
                animation.playAnimation();
                animation.loop(true);
                main.setBackgroundResource(R.drawable.rain_background);
            } else if (weatherData.get(index).getCondition().toLowerCase().contains("thunder")) {
                animation.setAnimation("thunderstorms_animation.json");
                animation.playAnimation();
                animation.loop(true);
                main.setBackgroundResource(R.drawable.thunderstorm_background);
            } else if (weatherData.get(index).getCondition().toLowerCase().contains("drizzle")) {
                animation.setAnimation("showers.json");
                animation.playAnimation();
                animation.loop(true);
                main.setBackgroundResource(R.drawable.rain_background);
            } else if (weatherData.get(index).getCondition().toLowerCase().contains("fog")) {
                animation.setAnimation("fog.json");
                animation.playAnimation();
                animation.loop(true);
                main.setBackgroundResource(R.drawable.fog_background);
            } else if (weatherData.get(index).getCondition().toLowerCase().contains("mist")) {
                animation.setAnimation("fog.json");
                animation.playAnimation();
                animation.loop(true);
                main.setBackgroundResource(R.drawable.mist_background);
            }  else if (weatherData.get(index).getCondition().toLowerCase().contains("blizzard")) {
                animation.setAnimation("fog.json");
                animation.playAnimation();
                animation.loop(true);
                main.setBackgroundResource(R.drawable.blizzard_background);
            } else if (weatherData.get(index).getCondition().toLowerCase().contains("hail")) {
                animation.setAnimation("fog.json");
                animation.playAnimation();
                animation.loop(true);
                main.setBackgroundResource(R.drawable.hail_background);
            } else if (weatherData.get(index).getCondition().toLowerCase().contains("sleet")) {
                animation.setAnimation("fog.json");
                animation.playAnimation();
                animation.loop(true);
                main.setBackgroundResource(R.drawable.sleet_background);
            }
        }

        findViewById(R.id.loading).setVisibility(View.GONE);

    }


}
