package com.example.hackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    EditText username,phone,password,retype, area, pin;
    Button register, login;
    AutoCompleteTextView city;
    DBlink DB;

    SharedPreferences sharedPreferences;

    String[] cities = {"Ariyalur", "Chennai", "Coimbatore", "Cuddalore", "Dharmapuri", "Dindigul", "Erode", "Kanchipuram",
            "Kanyakumari", "Karur", "Madurai", "Nagapattinam", "Nilgiris", "Namakkal", "Perambalur", "Pudukkottai",
            "Ramanathapuram", "Salem", "Sivaganga", "Tirupur", "Tiruchirappalli", "Theni", "Tirunelveli", "Thanjavur",
            "Thoothukudi", "Tiruvallur", "Tiruvarur", "Tiruvannamalai", "Vellore", "Viluppuram", "Virudhunagar"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences("com.example.hackathon.sharedpreferences", Context.MODE_PRIVATE);

        // Checking login status
        checkLogin();

        username = findViewById(R.id.username_login);
        phone = findViewById(R.id.phone_signup);
        password = findViewById(R.id.password_signup);
        retype = findViewById(R.id.repassword_signup);
        register = findViewById(R.id.register_signup);
        city = findViewById(R.id.city_signup);
        pin = findViewById(R.id.pin_signup);
        login = findViewById(R.id.login_signup);
        area = findViewById(R.id.area_signup);
        DB = new DBlink(this);


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = phone.getText().toString();
                String pass = password.getText().toString();
                String repass = retype.getText().toString();
                String user_name = username.getText().toString();
                String Area = area.getText().toString();
                String addres = city.getText().toString();
                String pin_no = pin.getText().toString();



                if (mobile.trim().equals("") || pass.trim().equals("") || repass.trim().equals("")||pin_no.trim().equals("") || user_name.trim().equals("") || addres.trim().equals(""))
                    Toast.makeText(getApplicationContext(),"fill all values",Toast.LENGTH_SHORT).show();


                else{
                    if (pass.equals(repass)){
                        //Boolean checkuser = DB.check_mobile(mobile);
                        requestRegister(user_name, pass, Area, addres, pin_no, mobile);
                    }else{
                        Toast.makeText(getApplicationContext(),"Password Not match",Toast.LENGTH_LONG).show();

                    }
                    if(pin_no.trim().length() != 6)
                        Toast.makeText(getApplicationContext(),"Enter correct pin number!!",Toast.LENGTH_LONG).show();

                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login_page.class);
                startActivity(intent);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_selectable_list_item, cities);
        city.setAdapter(adapter);
        city.setThreshold(1);


        city.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                city.showDropDown();
                return false;
            }
        });

        city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    hideSoftKeyboard();
                }
            }
        });
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) this.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    public void requestRegister(String user_name, String pass, String Area, String addres, String pin_no, String mobile) {
        String postUrl = GlobalEnvs.BASE_URL +  "api/user";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject postData = new JSONObject();
        try {

            postData.put("username", user_name);
            postData.put("password", pass);
            postData.put("area", Area);
            postData.put("city", addres);
            postData.put("pincode", pin_no);
            postData.put("phone", mobile);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!response.isNull("message")) {
                        String message = response.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        sharedPreferences.edit().putString("_id", response.getString("_id")).apply();
                        sharedPreferences.edit().putString("username", response.getString("username")).apply();
                        sharedPreferences.edit().putString("password", response.getString("password")).apply();
                        sharedPreferences.edit().putString("area", response.getString("area")).apply();
                        sharedPreferences.edit().putString("city", response.getString("city")).apply();
                        sharedPreferences.edit().putString("pincode", response.getString("pincode")).apply();
                        sharedPreferences.edit().putString("phone", response.getString("phone")).apply();
                        sharedPreferences.edit().putBoolean("isAdmin", false).apply();

                        checkLogin();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Registered Failed, Try again",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    public void checkLogin() {
        String pincode = sharedPreferences.getString("pincode", "");
        if (pincode != "") {
            Intent home_intent = new Intent(getApplicationContext(),home.class);
            startActivity(home_intent);
            // finish();
            finishAffinity();
        }
    }
}