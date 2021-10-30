package com.example.hackathon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class ReportsActivity extends AppCompatActivity {

    ImageView ipfsReportImageView;
    TextView departmentTextView;
    TextView reportDescriptionTextView;
    TextView reportCityTextView;
    TextView reportStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        ipfsReportImageView = findViewById(R.id.ipfsReportImageView);
        departmentTextView = findViewById(R.id.departmentTextView);
        reportDescriptionTextView = findViewById(R.id.reportDescription);
        reportCityTextView = findViewById(R.id.reportCity);
        reportStatusTextView = findViewById(R.id.reportStatus);

        Intent intent = getIntent();
        Report report = (Report) intent.getSerializableExtra("Report");

        if (report.getReportImageUrl() != null) {
            new DownloadImageTask(ipfsReportImageView).execute(report.getReportImageUrl());
        }

        /*Picasso.get()
                .load(report.getReportImageUri())
                .resize(60, 70)
                .into(ipfsReportImageView);*/

        departmentTextView.setText(Html.fromHtml("<font color='#000000'><b>Department: </b><br><font>" + report.dept));
        reportDescriptionTextView.setText(Html.fromHtml("<font color='#000000'><b>Description: </b><br><font>" + report.description));
        reportCityTextView.setText(Html.fromHtml("<font color='#000000'><b>City: </b><br><font>" + report.city));
        if (report.adminStatus && report.userStatus) {
            reportStatusTextView.setText("Solved");
            reportStatusTextView.setBackgroundColor(0xFF00D100);
        } else if (report.adminStatus || report.userStatus) {
            reportStatusTextView.setText("Solved and yet to be updated");
            reportStatusTextView.setBackgroundColor(0xFFFFBF00);
            reportStatusTextView.setTextColor(0xFF000000);
        } else {
            reportStatusTextView.setText("Unsolved");
            reportStatusTextView.setBackgroundColor(0xFFFF0000);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setBackgroundResource(android.R.color.transparent);
            bmImage.setImageBitmap(result);
        }
    }
}