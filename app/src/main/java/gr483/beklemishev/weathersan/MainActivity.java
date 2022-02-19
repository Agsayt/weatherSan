package gr483.beklemishev.weathersan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String wkey = "07ff3e9c9cb444feb8b82446212810";
    String city;

    EditText etCity;

    // Листы для просмотра истории
    ListView loadLV;
    ArrayList<ListForecast> lstHistory = new ArrayList<ListForecast>();
    ArrayAdapter<ListForecast> adpHistory;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCity = findViewById(R.id.etCityName);
        DBStatic.database = new DBHelper(this, "database", null, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    public void onQueryClick(View v){
        Thread thread = new Thread(() -> {
            StringBuilder result = new StringBuilder();
           try{
               city = etCity.getText().toString();

               URL url = new URL("http://api.weatherapi.com/v1/current.json?key=" + wkey + "&q=" + city + "&aqi=no");
               HttpURLConnection connection = (HttpURLConnection) url.openConnection();
               InputStream inputStream = connection.getInputStream();
               byte[] buffer = new byte[1024];
               while(true){
                   int length = inputStream.read(buffer,0,buffer.length);
                   if(length < 0) break;
                   result.append(new String(buffer, 0, length));
               }
               connection.disconnect();
               Log.d("json", result.toString());
               JSONObject info = new JSONObject(result.toString());


               Intent intent = new Intent(this,  SubActivity.class);
               intent.putExtra("info", info.toString());

               startActivityForResult(intent, 1); // Need to research new method

           } catch (MalformedURLException e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
               runOnUiThread(new Runnable() {
                   @Override
                   public void run() {
                       if (result.toString() == "")
                       {
                           etCity.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                           Toast.makeText(getApplicationContext(), "Проверьте правильность написания страны!", Toast.LENGTH_LONG).show();
                       }
                   }
               });
           } catch (JSONException e) {
               e.printStackTrace();
           }
        });
        thread.start();
    }

    private void OnViewHistory() {
        View customLayout = getLayoutInflater().inflate(R.layout.dialog_history, null);
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setTitle("История запросов");
        bld.setView(customLayout);

        Dialog dlg = bld.create();
        dlg.show();

        loadLV = customLayout.findViewById(R.id.forecastHistory);
        adpHistory = new ArrayAdapter<ListForecast>(this, android.R.layout.simple_list_item_1, lstHistory);

        loadLV.setAdapter(adpHistory);

        Button clear = customLayout.findViewById(R.id.Clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBStatic.database.remove("Forecasts", null);
                dlg.cancel();
            }
        });

//        loadLV.setOnItemClickListener((parent, _view, position, id) -> {
//            NetworkSettings n = adpNetwork.getItem(position);
//            address.setText(n.Address);
//            port.setText(String.valueOf(n.Port));
//            dlg.cancel();
//        });

        lstHistory.clear();
        lstHistory.addAll(DBStatic.database.getAllForecast());
        adpHistory.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.Settings: {
                final View customLayout = getLayoutInflater().inflate(R.layout.dialog_settings, null);
                AlertDialog.Builder bld = new AlertDialog.Builder(this);
                bld.setTitle("Настройки подключения!");
                bld.setView(customLayout);

                Dialog dlg = bld.create();

                EditText key = customLayout.findViewById(R.id.etApiKey);

                Button accept = customLayout.findViewById(R.id.bAcceptSettings);
                Button cancel = customLayout.findViewById(R.id.bCancelSettings);

                dlg.show();


                accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        wkey = String.valueOf(key.getText());
                        dlg.cancel();
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dlg.cancel();
                    }
                });
            }
            case R.id.history:
                OnViewHistory();
            /*case R.id.Save: {
                final View customLayout = getLayoutInflater().inflate(R.layout.dialog_imagesave, null);
                AlertDialog.Builder bld = new AlertDialog.Builder(this);
                bld.setTitle("Настройки подключения!");
                bld.setView(customLayout);

                LinearLayout preview = customLayout.findViewById(R.id.previewGridSave);
                EditText imageName = customLayout.findViewById(R.id.etImageNameToSave);

                Button save = customLayout.findViewById(R.id.bSaveImage);
                Button cancel = customLayout.findViewById(R.id.bCancelImageSave);

                Dialog dlg = bld.create();
                dlg.show();

                GridLayout prevGrid = new GridLayout(getApplicationContext());
                prevGrid.setRowCount(gridHeight);
                prevGrid.setColumnCount(gridWidth);
                List<Button> prevButtonsList = new ArrayList<>();
                FillGrid(gridWidth, gridHeight, prevGrid, prevButtonsList);
                for (int i = 0; i < prevButtonsList.size(); i++) {
                    prevButtonsList.get(i).setEnabled(false);
                    prevButtonsList.get(i).setTag(addedButtons.get(i).getTag());
                    prevButtonsList.get(i).setText(addedButtons.get(i).getText());
                    ColorDrawable viewColor = (ColorDrawable) addedButtons.get(i).getBackground();
                    int colorId = viewColor.getColor();
                    prevButtonsList.get(i).setBackgroundColor(colorId);
                }
                preview.addView(prevGrid);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int[] buffer = new int[prevGrid.getColumnCount() * prevGrid.getRowCount()];
                        for (int i = 0; i < prevButtonsList.size(); i++) {
                            ColorDrawable viewColor = (ColorDrawable) prevButtonsList.get(i).getBackground();
                            int colorId = viewColor.getColor();
                            buffer[i] = colorId;
                        }
                        int nid = StaticDb.database.getMaxIdForSavedImages() + 1;
                        StaticDb.database.addImage(nid, buffer, imageName.getText().toString());

                        Toast.makeText(getApplicationContext(), "Изображение успешно сохранено!", Toast.LENGTH_LONG).show();
                        dlg.cancel();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dlg.cancel();
                    }
                });
            }
            break;*/
            /*case R.id.Load: {
                Intent i = new Intent(this, StateActivity.class);
                startActivityForResult(i, 1);
            }
            break;*/
            /*case R.id.Live: {
                Intent i = new Intent(this, RealtimeImage.class);
                i.putExtra("width", gridWidth);
                i.putExtra("height", gridHeight);
                i.putExtra("address", destinationAddress);
                i.putExtra("port", destinationPort);

                int[] buffer = new int[16];
                for (int j = 0; j < 16; j++)
                {
                    buffer[j] = (int) addedButtons.get(j).getTag();
                }

                i.putExtra("buttons", (Serializable) buffer);
                startActivity(i);
            }
            break;*/
        }

        return super.onOptionsItemSelected(item);
    }
}