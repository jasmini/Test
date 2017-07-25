package com.jasmini.test.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jasmini.test.R;
import com.jasmini.test.model.UserDetails;
import com.jasmini.test.rectrofit.RetrofitServiceGenerator;
import com.jasmini.test.showlist.ListDetailsActivity;
import com.jasmini.test.utils.AppConstants;
import com.jasmini.test.utils.ConnectionDetector;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PERMISSION_REQUEST_CODE = 2;
    private static final int CAMERA_REQUEST = 1888;
    private TextView userName = null;
    private TextView firstName = null;
    private TextView lastName = null;
    private CircleImageView profileImage = null;
    private RelativeLayout editPhoto = null;
    private Button next = null;
    private ConnectionDetector connectionDetector = null;
    private ProgressBar progressBar = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userName = (TextView) findViewById(R.id.userName);
        firstName = (TextView) findViewById(R.id.firstName);
        lastName = (TextView) findViewById(R.id.lastName);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        profileImage = (CircleImageView) findViewById(R.id.profileImage);
        editPhoto = (RelativeLayout) findViewById(R.id.editPhoto);
        next = (Button) findViewById(R.id.submit);


        editPhoto.setOnClickListener(this);
        next.setOnClickListener(this);
        Log.e("onCreate", "onCreate");
        connectionDetector = new ConnectionDetector(this);
        if (connectionDetector.isConnectedToInternet()) {

            if (!checkPermission()) {
                requestPermission();
            } else {
                loadProfileDetails();
            }
        } else {
            Toast.makeText(MainActivity.this, "There is no internet connection.Please connect to internet.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editPhoto:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;
            case R.id.submit:
                Intent intent = new Intent(MainActivity.this, ListDetailsActivity.class);
                startActivity(intent);
                break;
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadProfileDetails();
                } else {
                    Toast.makeText(MainActivity.this, "To work properly app needs read external storage permission.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void loadProfileDetails() {
        progressBar.setVisibility(View.VISIBLE);
        UserdetailEndPoint userdetailEndPoint = RetrofitServiceGenerator.getClient(AppConstants.baseUrl).create(UserdetailEndPoint.class);

        userdetailEndPoint.getUserDetails().enqueue(new Callback<List<UserDetails>>() {
                                                        @Override
                                                        public void onResponse(Call<List<UserDetails>> call, Response<List<UserDetails>> response) {
                                                            Log.e("Response message=", "" + response.message());
                                                            if (response.body() != null) {
                                                                List<UserDetails> userDetails = response.body();
                                                                userName.setText("User name:" + userDetails.get(0).getUser_name());
                                                                firstName.setText("First name:" + userDetails.get(0).getFirst_name());
                                                                lastName.setText("Last Name:" + userDetails.get(0).getLast_name());
                                                                Glide.with(MainActivity.this)
                                                                        .load(userDetails.get(0).getUser_image()).placeholder(R.drawable.ic_person_black_24dp).error(R.drawable.ic_person_black_24dp)
                                                                        .fitCenter()
                                                                        .dontAnimate().dontTransform()
                                                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                                                        .skipMemoryCache(true)
                                                                        .into(profileImage);
                                                                progressBar.setVisibility(View.GONE);


                                                            } else {
                                                                progressBar.setVisibility(View.GONE);
                                                                Toast.makeText(MainActivity.this, "List is empty.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<List<UserDetails>> call, Throwable t) {
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(MainActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

        );
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap imagebitmap = (Bitmap) data.getExtras().get("data");

            try {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imagebitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);


                Glide.with(MainActivity.this)
                        .load(stream.toByteArray())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_person_black_24dp).error(R.drawable.ic_person_black_24dp)
                        .into(profileImage);

            } catch (Exception e) {
                Log.e("ImageException", e.getMessage());
            }
        }
    }
}


