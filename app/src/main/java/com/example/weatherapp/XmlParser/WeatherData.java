package com.example.weatherapp.XmlParser;

import java.util.ArrayList;
/*Name: Sumeiya Juma    ID: */s2110905
public class WeatherData {
    //variables
    private String date;
    private String minTemp;
    private String maxTemp;
    private String humidity;
    private String rain;
    private String wind;
    private String day;
    private String pressure;

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    private String sunrise;
    private String sunset;

    private String pic;
    private String condition;

    public String getDate() {
        return date;
    }
    public String getSunset(){return  sunset;}

    public String getSunrise(){return  sunrise;}

    public void setSunrise(String sunrise){ this.sunrise = sunrise;}
    public void setSunset(String sunset){this.sunset = sunset;}

    public void setDate(String date) {
        this.date = date;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(String minTemp) {
        this.minTemp = minTemp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getRain() {
        return rain;
    }

    public void setRain(String rain) {
        this.rain = rain;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
