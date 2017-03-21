package com.poziomlabs.firebasestore;

import android.content.Context;
import android.content.pm.PackageManager;
import android.Manifest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mMyRef;
    private WifiManager mWifi;
    private EditText mTitle;
    private EditText mBody;
    private List<ScanResult> mResults;

    private static final int REQUEST_INTERNET_ACCESS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInternetPermission();
        }
        mWifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        initWifi();
        mTitle = (EditText) findViewById(R.id.reviewTitle);
        mBody = (EditText) findViewById(R.id.reviewBody);
        Button save = (Button) findViewById(R.id.saveReview);
        Button get = (Button) findViewById(R.id.seeReview);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mMyRef = database.getReference().child("Users");

        save.setOnClickListener(this);
        get.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.saveReview) {
            if(!mWifi.isWifiEnabled())  initWifi();
            if(mResults != null) {
                Review review = new Review(mTitle.getText().toString(), mBody.getText().toString());
                for (ScanResult result : mResults) {
                    int level = WifiManager.calculateSignalLevel(result.level, 5);
                    if(level > 2) {
                        Log.d("Wifi Selected: " + result.SSID, "" + level);
                        review.setBssid(result.BSSID);
                        mMyRef.child(result.BSSID).child(review.getReviewId()).setValue(review);
                    } else {
                        Log.d("Wifi Rejected: " + result.SSID, "" + level);
                    }
                }
            }
        }
        if(view.getId() == R.id.seeReview) {
            mMyRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        for(DataSnapshot snapshot1: snapshot.getChildren()) {
                            Log.d("JSON: ", "" + snapshot1.getValue());
                            Review review = snapshot1.getValue(Review.class);
                            Log.d("Review: ", review.toString());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
    }

    private void initWifi() {
        mWifi.setWifiEnabled(true);
        mWifi.startScan();
        mResults = mWifi.getScanResults();
    }

    private void getInternetPermission() {
        boolean hasPermission1 = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED);
        boolean hasPermission2 = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED);
        boolean hasPermission3 = (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission1 || !hasPermission2 || !hasPermission3) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET, Manifest.permission.CHANGE_WIFI_STATE,
                            Manifest.permission.ACCESS_WIFI_STATE},
                    REQUEST_INTERNET_ACCESS);
        }
    }
}
