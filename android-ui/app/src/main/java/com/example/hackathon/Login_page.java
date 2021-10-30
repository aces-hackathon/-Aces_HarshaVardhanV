package com.example.hackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

public class Login_page extends AppCompatActivity {

    EditText Mobile,Password;
    Button Login_btn;
    DBlink DB;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Mobile = findViewById(R.id.username_login);
        Password = findViewById(R.id.password_login);
        Login_btn = findViewById(R.id.login_btn);
        DB = new DBlink(this);

        sharedPreferences = this.getSharedPreferences("com.example.hackathon.sharedpreferences", Context.MODE_PRIVATE);

        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mobile = Mobile.getText().toString();
                String pass = Password.getText().toString();

                if (mobile.trim().equals("") || pass.trim().equals(""))
                    Toast.makeText(getApplicationContext(),"Enter values",Toast.LENGTH_SHORT).show();
                else{
                    // Boolean correct = DB.check_mobile_pass(mobile, pass);
                    requestLoginUser(mobile, pass);
                }

            }
        });

    }

    public void requestLoginUser(String mobile, String pass) {
        String postUrl = GlobalEnvs.BASE_URL + "api/auth/user";
        String postAdminUrl = GlobalEnvs.BASE_URL + "api/auth/admin";
        boolean isAvailable = false;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JSONObject postData = new JSONObject();
        try {
            postData.put("password", pass);
            postData.put("phone", mobile);
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        JsonObjectRequest adminJsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postAdminUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getApplicationContext(),"Logged In!!", Toast.LENGTH_SHORT).show();
                // For admin
                try {
                    sharedPreferences.edit().putString("_id", response.getString("_id")).apply();
                    sharedPreferences.edit().putString("department", response.getString("department")).apply();
                    sharedPreferences.edit().putString("area", response.getString("area")).apply();
                    sharedPreferences.edit().putString("city", response.getString("city")).apply();
                    sharedPreferences.edit().putString("pincode", response.getString("pincode")).apply();
                    sharedPreferences.edit().putString("password", response.getString("password")).apply();
                    sharedPreferences.edit().putString("phone_1", response.getString("phone_1")).apply();
                    sharedPreferences.edit().putString("phone_2", response.getString("phone_2")).apply();
                    sharedPreferences.edit().putBoolean("isAdmin", true).apply();
                    sharedPreferences.edit().putString("adminPhone", postData.getString("phone")).apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("Response" , response.toString());
                Intent intent = new Intent(getApplicationContext(), home.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Invalid phone number or password",Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, postUrl, postData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    sharedPreferences.edit().putString("_id", response.getString("_id")).apply();
                    sharedPreferences.edit().putString("username", response.getString("username")).apply();
                    sharedPreferences.edit().putString("password", response.getString("password")).apply();
                    sharedPreferences.edit().putString("area", response.getString("area")).apply();
                    sharedPreferences.edit().putString("city", response.getString("city")).apply();
                    sharedPreferences.edit().putString("pincode", response.getString("pincode")).apply();
                    sharedPreferences.edit().putString("phone", response.getString("phone")).apply();
                    sharedPreferences.edit().putBoolean("isAdmin", false).apply();

                    Toast.makeText(getApplicationContext(), "Logged In!!", Toast.LENGTH_SHORT).show();
                    Log.i("Response", response.toString());
                    /*Intent intent = new Intent(getApplicationContext(), home.class);
                    startActivity(intent);
                    finish();*/
                    checkLogin();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(),"Invalid phone number or password",Toast.LENGTH_LONG).show();
                requestQueue.add(adminJsonObjectRequest);
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