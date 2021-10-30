package com.example.hackathon;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class new_report extends AppCompatActivity {


    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;

    Button bt_cam, location_bt;
    ImageView iv;
    String currentPhotoPath;
    TextView city_text, area_text, pin_text;
    Uri imageFileUri;
    EditText desc;

    FusedLocationProviderClient fusedLocationProviderClient;

    String[] tags = {"Water", "Eletricity", "Road", "Steer light failure"};
    AutoCompleteTextView atv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_report);

        bt_cam = findViewById(R.id.camera);
        iv = findViewById(R.id.image);
        atv = findViewById(R.id.tags);
        location_bt = findViewById(R.id.location_bt);
        city_text = findViewById(R.id.city_tv);
        area_text = findViewById(R.id.area_tv);
        pin_text = findViewById(R.id.pin_tv);
        desc = findViewById(R.id.description);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        location_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(new_report.this
                        , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(getApplicationContext(), "Please give access", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(new_report.this
                            , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }

        });

        bt_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askCameraPermission();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_selectable_list_item, tags);
        atv.setAdapter(adapter);
        atv.setThreshold(1);

        atv.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                atv.showDropDown();
                return false;
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();

                if (location != null) {

                    try {
                        Geocoder geocoder = new Geocoder(new_report.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                        /*Log.i("myaddr", addresses.get(0).getAdminArea());
                        Log.i("myaddr", addresses.get(0).getSubAdminArea());
                        Log.i("myaddr", addresses.get(0).getFeatureName() + "");
                        Log.i("myddr", addresses.get(0).getThoroughfare() + "");
                        Log.i("myddr", addresses.get(0).getSubThoroughfare() + "");*/

                        city_text.setText(Html.fromHtml(
                                "<font color='#000000'><b>City : </b><br><font>"
                                        + addresses.get(0).getLocality()
                        ));

                        String areaLoc = "";
                        if (addresses.get(0).getSubLocality() != null) {
                            areaLoc = addresses.get(0).getSubLocality();
                        } else {
                            areaLoc = addresses.get(0).getThoroughfare();
                        }

                        area_text.setText(Html.fromHtml(
                                "<font color='#000000'><b>Area : </b><br><font>"
                                        + areaLoc
                        ));

                        pin_text.setText(Html.fromHtml(
                                "<font color='#000000'><b>Pin : </b><br><font>"
                                        + addresses.get(0).getPostalCode()
                        ));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }


    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                iv.setBackgroundResource(android.R.color.transparent);
                iv.setImageURI(Uri.fromFile(f));

            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                imageFileUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(this, "Camare Permission is Required to Use Camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 750, 1280, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.PNG, 70, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public void sendReportToServer(View view) {
        String postUrl = GlobalEnvs.BASE_URL + "api/reports";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // For image
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageFileUri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap finalBitmap = bitmap;
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, postUrl,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject responseObj = new JSONObject(new String(response.data));
                            if (!responseObj.isNull("message")) {
                                String message = responseObj.getString("message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                                Intent homeIntent = new Intent(getApplicationContext(), home.class);
                                startActivity(homeIntent);
                                finishAffinity();
                            } else {
                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            }
                            // Toast.makeText(getApplicationContext(), responseObj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }) {

                    /*
                     * If you want to add more parameters with the image
                     * you can do it here
                     * here we have only one parameter with the image
                     * which is tags
                     * */
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("userId", GlobalEnvs.sharedPreferences.getString("_id", null));
                        params.put("fileName", System.currentTimeMillis() + "");
                        params.put("pin", pin_text.getText().toString().substring(7));
                        params.put("city", city_text.getText().toString().substring(8));
                        params.put("area", area_text.getText().toString().substring(8));
                        params.put("desc", desc.getText().toString());
                        params.put("dept", atv.getText().toString());
                        return params;
                    }

                    /*
                     * Here we are passing image by renaming it with a unique name
                     * */
                    @Override
                    protected Map<String, DataPart> getByteData() {
                        Map<String, DataPart> params = new HashMap<>();
                        long imagename = System.currentTimeMillis();
                        params.put("img", new DataPart(imagename + ".png", getFileDataFromDrawable(finalBitmap)));
                        return params;
                    }
        };


        volleyMultipartRequest.setRetryPolicy(
                new DefaultRetryPolicy(
                        0,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        );

        requestQueue.add(volleyMultipartRequest);
    }
}