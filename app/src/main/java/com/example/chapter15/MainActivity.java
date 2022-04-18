package com.example.chapter15;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    // we"ll make HTTP request to this URL to retrieve weather conditions
    String weatherWebserviceURL;

    ImageView weatherBackground;

    // Textview to show temperature and description
    TextView temperature, description ,humidity,sunrise,sunset,wind;

    // JSON object that contains weather information
    JSONObject jsonObj;

    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //link graphical items to variables
        temperature = (TextView) findViewById(R.id.temperature);
        description = (TextView) findViewById(R.id.description);
        humidity = (TextView) findViewById(R.id.humidity);
        sunrise = (TextView) findViewById(R.id.sunrise);
        sunset = (TextView) findViewById(R.id.sunset);
        wind = (TextView) findViewById(R.id.wind);

        weatherBackground = (ImageView) findViewById(R.id.weatherbackground);

        //drop down menu
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cities, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String city = parentView.getItemAtPosition(position).toString();
                Log.d("Naif",city);
                weatherWebserviceURL =
                        "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid=8de97d4e16d0d57d860cdb3a0b7a93d3&units=metric";
                weather(weatherWebserviceURL);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    public void weather(String url) {
        JsonObjectRequest jsonObj =
                new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Naif","Response received");
                Log.d("Naif",response.toString());
                try {
                    String town=response.getString("name");
                    Log.d("Naif-town",town);
                    // nested object
                    JSONObject jsonMain = response.getJSONObject("main");
                    //
                    double temp = jsonMain.getDouble("temp");
                    DecimalFormat df = new DecimalFormat("##");
                    Log.d("Naif-temp",String.valueOf(df.format(temp)+" °C"));
                    temperature.setText(String.valueOf(df.format(temp)+" °C"));

                    String humid = jsonMain.getString("humidity");
                    Log.d("Naif-humidity",String.valueOf(humid));
                    humidity.setText("Humidity: "+String.valueOf(humid)+"%");
                    //String townResponse = jsonMain.getString("name");
                    description.setText(town);

                    JSONObject jsonSys = response.getJSONObject("sys");

                    SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

                    long sunr = jsonSys.getLong("sunrise");
                    Log.d("Naif-sunrise",String.valueOf(sunr));
                    Log.d("Naif-sunrise",String.valueOf(localDateFormat.format(new Date(sunr*1000))+" GMT+03"));
                    sunrise.setText("Sunrise: "+String.valueOf(localDateFormat.format(new Date(sunr*1000))+" GMT+03"));

                    long suns = jsonSys.getLong("sunset");
                    Log.d("Naif-sunrise",String.valueOf(new Date(suns*1000)));
                    Log.d("Naif-sunset",String.valueOf(localDateFormat.format(new Date(suns*1000))+" GMT+03"));
                    sunset.setText("Sunset: "+String.valueOf(localDateFormat.format(new Date(suns*1000))+" GMT+03"));

                    JSONObject jsonWind = response.getJSONObject("wind");

                    String windS = jsonWind.getString("speed");
                    Log.d("Naif-wind",String.valueOf(windS));
                    wind.setText("Wind Speed: "+String.valueOf(windS)+" km/h");

                    /* sub categories as JSON arrays */
                    JSONArray jsonArray = response.getJSONArray("weather");
                    for (int i=0; i < jsonArray.length(); i++){
                        Log.d("Naif-array",jsonArray.getString(i));
                        JSONObject oneObject = jsonArray.getJSONObject(i);
                        String weather =
                                oneObject.getString("main");
                        Log.d("Naif-w",weather);

                        if (weather.equals("Clouds")){
                            Glide.with(MainActivity.this)
                                    .load("https://m.media-amazon.com/images/I/51XS+lSy+iL._AC_SL1000_.jpg")
                                    .into(weatherBackground);
                        }
                        else if (weather.equals("Clear")){
                            Glide.with(MainActivity.this)
                                    .load("https://www.gannett-cdn.com/-mm-/e5c305e00d80354d1c0350948b3ccc39c5d4956e/c=0-202-3867-2377/local/-/media/Salinas/2015/03/19/B9316661963Z.1_20150319105958_000_GLQA8VSAM.1-0.jpg")
                                    .into(weatherBackground);

                        }
                        else if (weather.equals("Dust")){
                            Glide.with(MainActivity.this)
                                    .load("https://move2turkey.com/wp-content/uploads/2021/04/تحذير-خبراء-الطقس-سوف-تبدأ-موجة-الغبار-الليلة-في-تركيا.jpg")
                                    .into(weatherBackground);

                        }
                        else if (weather.equals("Rain")){
                            Glide.with(MainActivity.this)
                                    .load("https://i.guim.co.uk/img/media/8132db117e863456488b553125383608d8c13264/0_265_2366_1419/master/2366.jpg?width=1200&height=1200&quality=85&auto=format&fit=crop&s=a646aa6afdee6cbea9460b4a660743ab")
                                    .into(weatherBackground);

                        }
                        else {
                            Glide.with(MainActivity.this)
                                    .load("https://nordicapis.com/wp-content/uploads/5-Best-Free-and-Paid-Weather-APIs-2019-e1587582023501.png")
                                    .into(weatherBackground);

                        }
                        }
                } catch (JSONException e){
                    e.printStackTrace();
                    Log.d("Receive Error",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Naif", "Error retrieving URL"+error.toString());
                error.toString();
            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObj);

    }
}