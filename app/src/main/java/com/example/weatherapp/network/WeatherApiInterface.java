package com.example.weatherapp.network;

import com.example.weatherapp.model.WeatherInformations;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiInterface {

    @GET("weather")
    Observable<WeatherInformations> getWeatherData(@Query("lat") String lat,
                                                   @Query("lon") String lng,
                                                   @Query("appid") String appid,
                                                   @Query("units") String unit);
}
