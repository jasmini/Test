package com.jasmini.test.showlist;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jasmini.test.R;
import com.jasmini.test.adapter.DetailsRecyclerAdapter;
import com.jasmini.test.model.ListDetails;
import com.jasmini.test.rectrofit.RetrofitServiceGenerator;
import com.jasmini.test.utils.AppConstants;
import com.jasmini.test.utils.ConnectionDetector;
import com.jasmini.test.utils.RecyclerViewClick;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListDetailsActivity extends AppCompatActivity implements RecyclerViewClick {
    private RecyclerView detailsRecycler = null;
    private DetailsRecyclerAdapter detailsRecyclerAdapter = null;
    private ConnectionDetector connectionDetector = null;
    private static final int PERMISSION_REQUEST_CODE = 3;
    private ProgressBar progressBar=null;
    private TextView descriptionTextView=null;
    private RecyclerViewClick recyclerViewClick=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        detailsRecycler = (RecyclerView) findViewById(R.id.detailsRecycler);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        descriptionTextView=(TextView)findViewById(R.id.descriptionTextView);
        recyclerViewClick=(RecyclerViewClick)this;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ListDetailsActivity.this, LinearLayoutManager.VERTICAL, false);
        detailsRecycler.setLayoutManager(mLayoutManager);
        connectionDetector = new ConnectionDetector(this);
        if (connectionDetector.isConnectedToInternet()) {

            if (!checkPermission()) {
                requestPermission();
            } else {
                loadList();
            }
        } else {
            Toast.makeText(ListDetailsActivity.this, "There is no internet connection.Please connect to internet.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ListDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(ListDetailsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(ListDetailsActivity.this, "Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(ListDetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadList();
                } else {
                    Toast.makeText(ListDetailsActivity.this, "To work properly app needs read external storage permission.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void loadList() {
        progressBar.setVisibility(View.VISIBLE);
        ListDetailsEndPoint listDetailsEndPoint = RetrofitServiceGenerator.getClient(AppConstants.baseUrl).create(ListDetailsEndPoint.class);

        listDetailsEndPoint.getListDetails().enqueue(new Callback<List<ListDetails>>() {
                                                         @Override
                                                         public void onResponse(Call<List<ListDetails>> call, Response<List<ListDetails>> response) {
                                                             Log.e("Response message=", "" + response.message());
                                                             if (response.body() != null) {
                                                                 List<ListDetails> listDetails = response.body();
                                                                 detailsRecyclerAdapter = new DetailsRecyclerAdapter(ListDetailsActivity.this, listDetails,recyclerViewClick);
                                                                 detailsRecycler.setAdapter(detailsRecyclerAdapter);
                                                                 progressBar.setVisibility(View.GONE);

                                                             } else {
                                                                 progressBar.setVisibility(View.GONE);
                                                                 Toast.makeText(ListDetailsActivity.this, "List is empty.", Toast.LENGTH_SHORT).show();
                                                             }
                                                         }

                                                         @Override
                                                         public void onFailure(Call<List<ListDetails>> call, Throwable t) {
                                                             progressBar.setVisibility(View.GONE);
                                                             Toast.makeText(ListDetailsActivity.this, "Server error", Toast.LENGTH_SHORT).show();
                                                         }
                                                     }

        );
    }

    @Override
    public void onClick(String description) {
        Log.e("----",description+"99");
        if(!TextUtils.isEmpty(description)) {
            descriptionTextView.setText(description);
        }
    }
}
