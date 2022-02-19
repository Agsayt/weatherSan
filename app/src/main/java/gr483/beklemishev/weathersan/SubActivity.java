package gr483.beklemishev.weathersan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SubActivity extends AppCompatActivity {


    TextView temp, fall, cloud, wind, windDir, pressure, feelslike, precip, date;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        temp = findViewById(R.id.temp);
        img = findViewById(R.id.icon);
        fall = findViewById(R.id.fallout);
        cloud = findViewById(R.id.cloud);
        wind = findViewById(R.id.windspeed);
        windDir = findViewById(R.id.winddirection);
        pressure = findViewById(R.id.pressure);
        precip = findViewById(R.id.precip);
        feelslike = findViewById(R.id.feelslike);
        date = findViewById(R.id.date);


        Intent intent = getIntent();

        try {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra("info"));
            String city = jsonObject.getJSONObject("location").getString("name");
            JSONObject curr = jsonObject.getJSONObject("current");
            if (curr.length() > 0) {
                String Date = curr.getString("last_updated");
                float tempc = (float) curr.getDouble("temp_c");
                float Wind = (float) curr.getDouble("wind_kph");
                String WindDir = curr.getString("wind_dir");
                float Pressure = (float) curr.getDouble("pressure_mb");
                float Precip = (float) curr.getDouble("precip_mm");
                float Feelslike = (float) curr.getDouble("feelslike_c");
                float Cloud = (float) curr.getDouble("cloud");

                JSONObject cond = curr.getJSONObject("condition");
                String fallout = cond.getString("text");
                String icon = cond.getString("icon");
                URL url1 = new URL("https:" + icon);
                Thread thread = new Thread(() -> {

                    Bitmap bmp = null;
                    byte[] array = DBStatic.database.FindIcon(url1.toString());
                    bmp = BitmapFactory.decodeByteArray(array, 0,array.length);
                    if (bmp != null)
                    {
                        Bitmap finalBmp = bmp;
                        runOnUiThread(() -> {
                            img.setImageBitmap(finalBmp);
                        });
                    }
                });
                thread.start();




                date.setText(String.valueOf(Date));
                temp.setText(String.valueOf(tempc) + " C");
                feelslike.setText(String.valueOf(Feelslike) + " C");
                wind.setText(String.valueOf(Wind) + " km/h");
                windDir.setText(String.valueOf(WindDir));
                pressure.setText(String.valueOf(Pressure) + " millibar");
                precip.setText(String.valueOf(Precip) + " mm");
                cloud.setText(String.valueOf(Cloud));
                fall.setText(fallout);


                DBStatic.database.addForecast(Date, city, tempc, Feelslike, Wind, Pressure, Precip, Cloud, WindDir);
            }
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }

    }


    public void ClickBack(View v)
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}