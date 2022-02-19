package gr483.beklemishev.weathersan;

public class ListForecast {
    public int id;
    public String city;
    public String date;
    public float temprature;
    public float feelslike;
    public float cloud;
    public float windKmh;
    public float pressure;
    public float precip;
    public String windDir;


    public String toString() {
        return id + "  " + date + "  " + city
                + "\nTemperature: " + temprature + " °C"
                + "\nFeelslike: "+ feelslike + " °C"
                + "\nWind: " + windKmh + " km/h"
                + "\nPressure: " + pressure + " millibars"
                + "\nPrecipitation: " + precip + " mm"
                + "\nCloud: " + cloud + " %"
                + "\nWind direction: " + windDir + "";
    }
}