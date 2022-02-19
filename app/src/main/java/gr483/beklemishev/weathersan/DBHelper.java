package gr483.beklemishev.weathersan;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE Keys (id INT, keys TXT);";
        db.execSQL(sql);

        sql = "CREATE TABLE StoredIcons (id INT, icon BLOB, url TXT);";
        db.execSQL(sql);

        sql = "CREATE TABLE Forecasts (id INT, date TXT,city TXT,temprature FLOAT,Feelslike FLOAT, windKmh FLOAT, pressure FLOAT, precip FLOAT, cloud FLOAT, windDir String);";
        db.execSQL(sql);
    }

    public void addKey(String key)
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO Keys VALUES ("+getMaxId("Keys")+", '"+key+"');";
        db.execSQL(sql);
    }

    public ArrayList<ListKey> getAllKeys()
    {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<ListKey> lst = new ArrayList<ListKey>();
        String sql = "SELECT id,keys FROM KeySettings;";
        Cursor cur = db.rawQuery(sql,null);
        if(cur.moveToFirst()){
            do {
                ListKey n = new ListKey();
                n.id = cur.getInt(0);
                n.Key = cur.getString(1);
                lst.add(n);
            } while (cur.moveToNext());
        }
        cur.close();
        return lst;
    }

    public void remove(String table, Integer keyid)
    {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "";
        if (keyid != null)
        {
            sql = "DELETE FROM "+table+" Where id = " + keyid +";";
        }
        else
            sql = "DELETE FROM "+table+";";

        db.execSQL(sql);
    }

    public void addForecast(String date, String city, float temperature, float Feelslike, float windKmh, float pressure, float precip, float cloud, String windDir)
    {

        SQLiteDatabase db = getWritableDatabase();
        String sql = "INSERT INTO Forecasts VALUES ("+getMaxId("Forecasts")+",'"+date+"','"+city+"','"+temperature+"','"+Feelslike+"','"+windKmh+"','"+pressure+"','"+precip+"','"+cloud+"', '"+windDir+"');";
        db.execSQL(sql);
    }

    public ArrayList<ListForecast> getAllForecast()
    {
        ArrayList<ListForecast> lst = new ArrayList<ListForecast>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT id,date,city,temprature,feelslike,windKmh,pressure,precip,cloud,windDir FROM Forecasts ORDER By date desc;";
        Cursor cur = db.rawQuery(sql,null);
        if(cur.moveToFirst()){
            do {
                ListForecast n = new ListForecast();
                n.id = cur.getInt(0);
                n.date = cur.getString(1);
                n.city = cur.getString(2);
                n.temprature = cur.getFloat(3);
                n.feelslike = cur.getFloat(4);
                n.windKmh = cur.getFloat(5);
                n.pressure = cur.getFloat(6);
                n.precip = cur.getFloat(7);
                n.cloud = cur.getFloat(8);
                lst.add(n);
            } while (cur.moveToNext());
        }
        cur.close();
        return lst;
    }

    @NonNull
    private String getMaxId(String table){
        int maxid = 0;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT Max(id) FROM "+ table +";";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst()){
            maxid = cur.getInt(0);
            cur.close();

        }
        cur.close();
        return String.valueOf(maxid);
    }

    public byte[] FindIcon(String url)
    {
        byte[] icon = null;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT icon FROM StoredIcons Where url = '"+url+"';";
        Cursor cur = db.rawQuery(sql, null);
        if (cur.moveToFirst()){
            icon = cur.getBlob(0);
            cur.close();

        }
        else
        {
            icon = AddIcon(url);
        }
        cur.close();
        return icon;
    }

    @Nullable
    private byte[] AddIcon(String url) {
        try {
            URL imageUrl = new URL(url);
            URLConnection icon = imageUrl.openConnection();

            InputStream is = icon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            byte[] baf = new byte[5000];
            int current = 0;
            int i = 0;
            while ((current = bis.read()) != -1) {
                baf[i]= ((byte) current);
                i++;
            }

            SQLiteDatabase db = getWritableDatabase();

            String sql = "INSERT INTO StoredIcons VALUES ("+getMaxId("StoredIcons") + 1 +",?,?)";
            SQLiteStatement insertStmt = db.compileStatement(sql);
            insertStmt.clearBindings();
            insertStmt.bindString(2, url);
            insertStmt.bindBlob(1, baf);
            insertStmt.executeInsert();

            db.execSQL(sql);

            Log.d("BD_ICON", "New icon added!");

            return baf;
        } catch (Exception e) {
            Log.d("ImageManager", "Error: " + e.toString());
        }
        return null;


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
