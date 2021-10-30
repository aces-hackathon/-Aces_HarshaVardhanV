package com.example.hackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class home extends AppCompatActivity {

    Button add_report;
    ListView reportsListView;
    ArrayList<String[]> reportList;
    ArrayAdapter<String[]> adapter;
    ArrayList<Report> reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        add_report = findViewById(R.id.add_report_home);
        reportsListView = (ListView) findViewById(R.id.reportsListView);

        reportList = new ArrayList<>();
        adapter = new ArrayAdapter<String[]>(this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                reportList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // View row = super.getView(position, convertView, parent);

                LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View row = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);

                TextView text1 = (TextView) row.findViewById(android.R.id.text1);
                TextView text2 = (TextView) row.findViewById(android.R.id.text2);

                String[] item = reportList.get(position);
                text1.setText(item[0]);
                text2.setText(item[1]);

                Report report = reports.get(position);
                System.out.println(report.adminStatus + " " + report.userStatus);

                if (report.adminStatus == true && report.userStatus == true) {
                    text1.setTextColor(Color.GRAY);
                    text2.setTextColor(Color.GRAY);
                    row.setBackgroundColor(Color.LTGRAY);
                } else {
                    text1.setTextColor(Color.BLACK);
                    text2.setTextColor(Color.GRAY);
                }
                return row;
            }
        };

        // Setting the adapter
        reportsListView.setAdapter(adapter);

        reportsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Intent
                Intent reportIntent = new Intent(getApplicationContext(), ReportsActivity.class);
                reportIntent.putExtra("Report", reports.get(i));
                startActivity(reportIntent);
            }
        });

        // Get reports
        getReports();

        add_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent report_intent = new Intent(getApplicationContext(), new_report.class);
                startActivity(report_intent);
            }
        });
    }

    public void getReports() {
        String url = GlobalEnvs.BASE_URL + "api/reports";
        reports = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int n = response.length();
                    for (int i = 0; i < n; i++) {
                        JSONObject jsonObject = response.getJSONObject(i);
                        Report report = new Report();
                        report.reportId = jsonObject.getString("reportId");
                        report.userId = jsonObject.getString("userId");
                        report.reportHash = jsonObject.getString("reportHash");
                        report.description = jsonObject.getString("description");
                        report.dept = jsonObject.getString("dept");
                        report.city = jsonObject.getString("city");
                        report.userStatus = jsonObject.getBoolean("userStatus");
                        report.adminStatus = jsonObject.getBoolean("adminStatus");
                        report.uploadTime = jsonObject.getString("uploadTime");
                        report.uploader = jsonObject.getString("uploader");

                        if (i == n-1) {
                            report.userStatus = true;
                            report.adminStatus = true;
                        }

                        reports.add(report);
                        String truncatedDesc = report.description.length() <= 50 ? report.description : report.description.substring(0, 50) + "...";
                        reportList.add(putData(truncatedDesc, "City: " + report.city + "\r\t" + "Department: " + report.dept));

                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }
        );

        requestQueue.add(jsonArrayRequest);
    }

    private String[] putData(String desc, String city) {
        String[] item = {desc, city};
        return item;
    }
}