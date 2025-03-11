package com.example.newsreader;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
SharedPreferences sharedPreferences;
ArrayList<String> urls =  new ArrayList<>();

ArrayList<String> titles  = new ArrayList<>();
ArrayList<String> texts = new ArrayList<>();
ArrayAdapter<String> arrayAdapter;
SQLiteDatabase stories;
ListView myListView;
Button loadButton;
ProgressBar progress;
LinearLayout linearLayout;

    public void setStories(){
        String stringUrl = "";
        String stringTitle = "";
        String stringHtml="";



        Cursor c = stories.rawQuery("SELECT * FROM story", null);
        if(c != null && c.moveToFirst()) {
            urls.clear();
            texts.clear();
            titles.clear();
            int urlIndex = c.getColumnIndex("url");
            int titleIndex = c.getColumnIndex("title");
            int htmlIndex = c.getColumnIndex("html");
            Log.i("ResultDB", "the html Index "+ String.valueOf(htmlIndex));

            do {
               // Log.i("ResultDB", c.getString(id));
               //Log.i("ResultDB", c.getString(titleIndex));
                //Log.i("ResultDB", c.getString(urlIndex));
                stringUrl = c.getString(urlIndex);
                stringTitle = c.getString(titleIndex);
                stringHtml = c.getString(htmlIndex);

                urls.add(stringUrl);
                titles.add(stringTitle);
                texts.add(stringHtml);


            } while (c.moveToNext());
            arrayAdapter.notifyDataSetChanged();

        }
        else{
            Log.i("ResultDB", "C is null");
        }
        if(c != null){


            c.close();
        }


    }
    //function to make the header or top bar disappear while stories are being set
    public void offScreen(){
        runOnUiThread(() -> {
            // Hide the button
            linearLayout.setVisibility(INVISIBLE);

            myListView.setVisibility(INVISIBLE);
            progress.setVisibility(VISIBLE);

            // Update SharedPreferences


            // Initialize the activity after the button is hidden
            new CountDownTimer(2000, 1000) {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    initializeActivity();
                }
            }.start();

        });

    }
    public void onScreen(){
        linearLayout.setVisibility(VISIBLE);
        myListView.setVisibility(VISIBLE);
        progress.setVisibility(INVISIBLE);
    }

    public void getStory(View view){
        sharedPreferences.edit().putBoolean("mode", false).apply();

        offScreen();
    }



public  class ReadPaper extends AsyncTask<String , Void, String> {


    @Override
    protected String doInBackground(String... urls) {

        String result = "";
        try {
            URL url = new URL(urls[0]);
            HttpsURLConnection conne = (HttpsURLConnection) url.openConnection();
            InputStream in = conne.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);
            stories.execSQL("DELETE FROM story");

            int data = reader.read();
            while (data != -1) {
                char current = (char) data;
                result += current;
                data = reader.read();
            }
            in.close();
            JSONArray jsonArray =  new JSONArray(result);
            for(int i = 0; i < Math.min(10, jsonArray.length()); i++){
             //   Log.i("ResultDB", jsonArray.getString(i));

                result = "";
                url = new URL("https://hacker-news.firebaseio.com/v0/item/"+jsonArray.getString(i)+".json?print=pretty");
                conne = (HttpsURLConnection) url.openConnection();
                in = conne.getInputStream();
                reader = new InputStreamReader(in);
                data = reader.read();
                while (data != -1) {

                    char current = (char) data;
                    result += current;
                    data = reader.read();

                }
                JSONObject jsonObject = new JSONObject(result);
               // Log.i("ResultDB", jsonObject.getString("title"));
                //Log.i("ResultDB", jsonObject.getString("url"));
                String theUrl = jsonObject.getString("url");
                String theTitle = jsonObject.getString("title");

                StringBuilder stringBuilder = new StringBuilder();
                url = new URL(theUrl);
                conne = (HttpsURLConnection) url.openConnection();

                try {
                    in = conne.getInputStream();
                    reader = new InputStreamReader(in);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line); // Append each line with a newline
                    }

                } catch (Exception e) {
                    Log.i("ResultDB", "this is three 3");
                    e.printStackTrace();
                }


                String htmlContent = stringBuilder.toString();
                // Prepare the SQL query to insert the data into the database

                 String insertQuery = "INSERT INTO story (title, url,html) VALUES (?,?,?) ";
                 SQLiteStatement statement = stories.compileStatement(insertQuery);
                 statement.bindString(1, theTitle);
                 statement.bindString(2, theUrl);
                 statement.bindString(3, stringBuilder.toString());
                 statement.execute();





            }




        } catch (Exception e) {
            e.printStackTrace();
            Log.i("ResultDB", "Failed but what now");



            return null;


        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {

        if(s != null){
            setStories();
            Log.i("ResultDB", "so s was null ");
        }
        onScreen();

    }

    // Execute AsyncTask





        }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadButton = findViewById(R.id.myButton);
        progress = findViewById(R.id.progressBar);
        myListView = findViewById(R.id.myListView);
        linearLayout = findViewById(R.id.linearLayout);
        initializeActivity();
    }

        private void initializeActivity(){

            sharedPreferences = this.getSharedPreferences("com.example.newsreader", Context.MODE_PRIVATE);
            stories = this.openOrCreateDatabase("Stories", MODE_PRIVATE, null);
            stories.execSQL("CREATE TABLE IF NOT EXISTS story (id INTEGER PRIMARY KEY,title TEXT, url TEXT, html TEXT)");
            Log.i("ResultDB", "the value of shared reference is "+sharedPreferences.getBoolean("mode",false));


            arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item, titles);
            myListView.setAdapter(arrayAdapter);
            if(!InternetCheck.isInternetAvailable(this)){
                Log.i("ResultDB", "No Internet so not updating database");
                sharedPreferences.edit().putBoolean("mode", true);
            }


            if(!sharedPreferences.getBoolean("mode",false)) {
                 // Drop the table
                stories.execSQL("CREATE TABLE IF NOT EXISTS story (id INTEGER PRIMARY KEY, title TEXT, url TEXT, html TEXT)"); // Recreate the table



                ReadPaper paper = new ReadPaper();

                    paper.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");



                sharedPreferences.edit().putBoolean("mode", true).apply();
                Log.i("ResultDB", "recreating");
            }
            else{
                Log.i("ResultDB", "creating");
                setStories();
                onScreen();

            }

            Log.i("ResultDB", "Arrived here");

            arrayAdapter.notifyDataSetChanged();



            // Log.i("ResultDB", urls.toString());
            // Log.i("ResultDB", titles.toString());
            // Log.i("ResultDB", texts.toString());

            myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Log.i("ResultDB", urls.get(i));
                    Log.i("ResultDB", titles.get(i));
                    Log.i("ResultDB", texts.get(i));
                    String htmlContent = texts.get(i);

                    sharedPreferences.edit().putString("html",htmlContent).apply();
                    Intent intent = new Intent(getApplicationContext(), ReadStory.class);
                    // intent.putExtra("html", texts.get(i));
                    intent.putExtra("url", urls.get(i));
                    startActivity(intent);

                }
            });
        }


    }

