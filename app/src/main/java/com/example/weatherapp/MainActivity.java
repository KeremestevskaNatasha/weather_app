package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import static com.example.weatherapp.common.Common.current_location;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherapp.common.Common;
import com.example.weatherapp.model.WeatherInformations;
import com.example.weatherapp.network.RetrofitClient;
import com.example.weatherapp.network.WeatherApiInterface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity  {


    private final static String TAG = "MainActivity";

    TextInputEditText textInputEditText;
    Button button;
    ImageView imageView;
    TextView country_yt, city_yt, temp_yt;
    TextView sunrise_tv, sunset_tv, humidity_tv, pressure_tv, longitude_tv, latitude_tv;

    ConstraintLayout weather_panel;
    CompositeDisposable compositeDisposable;
    WeatherApiInterface mService;

    private String sCity;
    double latitude;
    double longitude;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getClient();
        mService = retrofit.create(WeatherApiInterface.class);



        imageView = findViewById(R.id.imageButton);
        textInputEditText = findViewById(R.id.editTextCityName);
        button = findViewById(R.id.button);
        country_yt = findViewById(R.id.country);
        city_yt = findViewById(R.id.city);
        temp_yt = findViewById(R.id.temperature);

        sunrise_tv = findViewById(R.id.sunrise2);
        sunset_tv = findViewById(R.id.sunset2);
        humidity_tv = findViewById(R.id.humidity2);
        pressure_tv = findViewById(R.id.pressure2);
        longitude_tv = findViewById(R.id.longitude2);
        latitude_tv = findViewById(R.id.latitude2);

        weather_panel = findViewById(R.id.weather_panel);


        Dexter.withContext(MainActivity.this).withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {


                        if (multiplePermissionsReport.areAllPermissionsGranted()) {

                            buildLocationRequest();
                            buildLocationCallBack();

                            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED && ActivityCompat
                                    .checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                                return;

                            }
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {


                    }
                }).check();

    }

    public void onClick(){
            button.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   sCity = textInputEditText.getText().toString();
                   getWeatherInfo();
                  Log.d(TAG, "onClick: " + sCity);
                   //Hide Soft Keyboard
                   InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
            });
           //Enter key monitoring
        textInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
               @Override
               public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                   if (i == EditorInfo.IME_ACTION_UNSPECIFIED) {
                       sCity = textInputEditText.getText().toString();
                       getWeatherInfo();
                        Log.d(TAG, "onClick: " + sCity);
                       //Hide Soft Keyboard
                     InputMethodManager imm = (InputMethodManager) getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
                      imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                 }
                  return false;
                }
            });
       }

    private void getWeatherInfo() {


        compositeDisposable.add(mService.getWeatherData(String.valueOf(current_location.getLatitude()),
                String.valueOf(current_location.getLongitude()),
                Common.APP_ID, "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherInformations>() {


                               @Override
                               public void accept(WeatherInformations weatherInformations) throws Exception {


                                   Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                                           .append(weatherInformations.getWeather()
                                                   .get(0).getIcon())
                                           .append(".png").toString()).into(imageView);
                                   country_yt.setText(weatherInformations.getName());
                                   city_yt.setText(weatherInformations.getName());
                                   temp_yt.setText(new StringBuilder(
                                           String.valueOf(weatherInformations.getMain().getTemp())).append("°C").toString());

                                   humidity_tv.setText(new StringBuilder(
                                           String.valueOf(weatherInformations.getMain().getHumidity())).append("hpa").toString());
                                   pressure_tv.setText(new StringBuilder(
                                           String.valueOf(weatherInformations.getMain().getPressure())).append("%").toString());

                                   sunrise_tv.setText(Common.convertUnixToHour(weatherInformations.getSys().getSunrise()));
                                   sunset_tv.setText(Common.convertUnixToHour(weatherInformations.getSys().getSunset()));


                               }
                           }, new Consumer<Throwable>() {
                               @Override
                               public void accept(Throwable throwable) throws Exception {
                                   Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                           }

                )
        );
    }

    private void buildLocationCallBack() {

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                current_location = locationResult.getLastLocation();
                getWeatherInfo();


                Log.d("Location", locationResult
                        .getLastLocation().getLatitude() + "/" + locationResult
                        .getLastLocation().getLongitude());

            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10.0f);
    }


}

