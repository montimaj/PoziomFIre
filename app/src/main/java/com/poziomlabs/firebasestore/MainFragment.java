package com.poziomlabs.firebasestore;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment implements View.OnClickListener {

    private WifiManager mWifi;
    private ProgressBar mProgressBar;
    private ReviewAdapter mReviewAdapter;
    private ArrayList<String> mSelectedWifis;

    private static ArrayList<Review> sReviewList = new ArrayList<>();
    private static DatabaseReference sMyRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWifi = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        initWifi();
        initFirebase();

        ListView listView = (ListView) view.findViewById(R.id.listView);
        mReviewAdapter = new ReviewAdapter(getActivity(), R.layout.content_review, sReviewList);
        mProgressBar = (ProgressBar) view.findViewById(R.id.Progress);
        FloatingActionButton fab1 = (FloatingActionButton) view.findViewById(R.id.floatingActionButton1);
        FloatingActionButton fab2 = (FloatingActionButton) view.findViewById(R.id.floatingActionButton2);

        listView.setAdapter(mReviewAdapter);
        listView.setEmptyView(view.findViewById(R.id.Progress));

        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.floatingActionButton2) {
            LocalReview review = new LocalReview();
            review.setSelectedWifis(mSelectedWifis);
            ReviewFragment reviewFragment = new ReviewFragment();
            reviewFragment.setReview(review);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, reviewFragment)
                    .addToBackStack("Reviews")
                    .commit();
        } else {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, new DraftsFragment())
                    .addToBackStack("Reviews")
                    .commit();
        }
    }

    private void initWifi() {
        mWifi.setWifiEnabled(true);
        mWifi.startScan();
        List<ScanResult> results = mWifi.getScanResults();
        mSelectedWifis = new ArrayList<>();
        for (ScanResult result : results) {
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
        sMyRef = database.getReference().child("Users");
        sMyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(snapshot.hasChildren()) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                Review review = snapshot1.getValue(Review.class);
                                if (mSelectedWifis.contains(review.getBssid()) && !sReviewList.contains(review)) {
                                    sReviewList.add(review);
                                    mReviewAdapter.notifyDataSetChanged();
                                }
                            }
                        } else clearListView();
                    }
                } else clearListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void clearListView() {
        sReviewList.clear();
        mReviewAdapter.clear();
        mReviewAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(),"Sorry! No reviews to show", Toast.LENGTH_SHORT).show();
        mProgressBar.setVisibility(View.GONE);
    }

    static ArrayList<Review> getReviewList() { return sReviewList; }
    static DatabaseReference getDatabaseReference() { return sMyRef; }
}
