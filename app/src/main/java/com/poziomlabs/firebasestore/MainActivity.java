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
import android.view.View;;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference mMyRef;
    private WifiManager mWifi;
    private EditText mTitle;
    private EditText mBody;
    private List<ScanResult> mResults;
    private CustomAdapter mCustomAdapter;
    private ArrayList<String> mSelectedWifis;

    private static ArrayList<Review> sReviewList = new ArrayList<>();
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
        initFirebase();

        mTitle = (EditText) findViewById(R.id.reviewTitle);
        mBody = (EditText) findViewById(R.id.reviewBody);
        ListView listView = (ListView) findViewById(R.id.listView);
        mCustomAdapter = new CustomAdapter(this, R.layout.content_review, sReviewList);
        ImageButton save = (ImageButton) findViewById(R.id.saveReview);

        listView.setAdapter(mCustomAdapter);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mWifi.isWifiEnabled())  initWifi();
                String title = mTitle.getText().toString();
                String body = mBody.getText().toString();
                if(mResults != null && title.length() > 0 && body.length() > 0) {
                    Review review = new Review(title, body);
                    saveReview(review);
                }
            }
        });
    }

    private void initWifi() {
        mWifi.setWifiEnabled(true);
        mWifi.startScan();
        mResults = mWifi.getScanResults();
        mSelectedWifis = new ArrayList<>();
        for (ScanResult result : mResults) {
            int level = WifiManager.calculateSignalLevel(result.level, 5);
            if(level > 2) {
                Log.d("Wifi Selected: " + result.SSID, "" + level);
                mSelectedWifis.add(result.BSSID);
            } else {
                Log.d("Wifi Rejected: " + result.SSID, "" + level);
            }
        }
    }

    private void initFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mMyRef = database.getReference().child("Users");
        mMyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(snapshot.hasChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Review review = snapshot1.getValue(Review.class);
                                if (mSelectedWifis.contains(review.getBssid()) && !sReviewList.contains(review)) {
                                    sReviewList.add(review);
                                    mCustomAdapter.notifyDataSetChanged();
                                }
                            }
                        } else removeList();
                    }
                } else removeList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void saveReview(Review review) {
        for (String bssid : mSelectedWifis) {
            review.setBssid(bssid);
            mMyRef.child(bssid).child(review.getReviewId()).setValue(review);
        }
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

    private void removeList() {
        sReviewList.clear();
        mCustomAdapter.clear();
        mCustomAdapter.notifyDataSetChanged();
    }

    static ArrayList<Review> getReviewList() { return sReviewList; }
}
