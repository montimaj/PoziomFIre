package com.poziomlabs.firebasestore;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainFragment extends Fragment implements View.OnClickListener {

    private ProgressBar mProgressBar;
    private ReviewAdapter mReviewAdapter;
    private TextView mTextView;
    private ArrayList<String> mSelectedWifis;
    private ImageView mImageView;
    private AlertDialog mAlertDialog;
    private User mUser;

    private static ArrayList<Review> sReviewList = new ArrayList<>();
    private static DatabaseReference sMyRef;
    private static final String[] ADMIN_USERS = {"rachit.iitkgp@gmail.com", "monti.majumdar@gmail.com"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initWifi();
        initFirebase();
        ListView listView = (ListView) view.findViewById(R.id.listView);
        mReviewAdapter = new ReviewAdapter(getActivity(), R.layout.content_review, sReviewList);
        mProgressBar = (ProgressBar) view.findViewById(R.id.Progress);
        mImageView = (ImageView) view.findViewById(R.id.imageView);
        FloatingActionButton fab1 = (FloatingActionButton) view.findViewById(R.id.floatingActionButton1);
        FloatingActionButton fab2 = (FloatingActionButton) view.findViewById(R.id.floatingActionButton2);
        FloatingActionButton fab3 = (FloatingActionButton) view.findViewById(R.id.floatingActionButton3);

        if(!getReviewList().isEmpty()) {
            mImageView.setImageResource(R.mipmap.savereview);
        } else mImageView.setImageResource(R.mipmap.no_review);

        listView.setAdapter(mReviewAdapter);
        listView.setEmptyView(view.findViewById(R.id.Progress));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Review review = (Review) adapterView.getAdapter().getItem(i);
                displayReviewDialog(review);
            }
        });

        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        if(view.getId() == R.id.floatingActionButton2) {
            LocalReview review = new LocalReview();
            review.setSelectedWifis(mSelectedWifis);
            review.setUser(mUser);
            ReviewFragment reviewFragment = new ReviewFragment();
            reviewFragment.setReview(review);
            manager.beginTransaction()
                    .replace(R.id.content_frame, reviewFragment)
                    .addToBackStack("Reviews")
                    .commit();
        } else if(view.getId() == R.id.floatingActionButton1){
            manager.beginTransaction()
                    .replace(R.id.content_frame, new DraftsFragment())
                    .addToBackStack("Reviews")
                    .commit();
        } else {
            SavedReviewFragment savedReviewFragment = new SavedReviewFragment();
            savedReviewFragment.setUser(mUser);;
            manager.beginTransaction()
                    .replace(R.id.content_frame, savedReviewFragment)
                    .addToBackStack("Reviews")
                    .commit();
        }
    }

    private void initWifi() {
        final WifiManager wifiManager = (WifiManager) getContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        wifiManager.startScan();
        List<ScanResult> results = wifiManager.getScanResults();
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

    private void displayReviewDialog(final Review review) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View dialogView = layoutInflater.inflate(R.layout.content_reviewitem, null);
        mAlertDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setTitle("Review Description")
                .setMessage(review.toString())
                .setPositiveButton("OK", null)
                .setCancelable(true)
                .create();

        RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingBar3);
        ratingBar.setRating(review.getRating());
        ratingBar.setIsIndicator(true);

        SwitchCompat switchCompat = (SwitchCompat) dialogView.findViewById(R.id.switch1);
        if(isAdmin()) {
            switchCompat.setChecked(review.getModeratorFlag());
            switchCompat.setVisibility(View.VISIBLE);
            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ArrayList<String> wifis = review.getSelectedWifis();
                    if(isChecked) {
                        review.setModeratorFlag(true);
                    } else  review.setModeratorFlag(false);
                    for (String bssid : wifis) {
                        sMyRef.child(bssid).child(review.getReviewId()).setValue(review);
                    }
                }
            });
        } else  switchCompat.setVisibility(View.GONE);

        mAlertDialog.show();
        mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialog.dismiss();
            }
        });
    }

    private void initFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        sMyRef = database.getReference().child("Users");
        sMyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(isAdmin() || mSelectedWifis.contains(snapshot.getKey())) {
                            if (snapshot.hasChildren()) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    Review currentReview = snapshot1.getValue(Review.class);
                                    if(!isAdmin()) {
                                        if(currentReview.getModeratorFlag() || currentReview.getUser().equals(mUser)) {
                                            showReviews(currentReview);
                                        }
                                    } else showReviews(currentReview);
                                }
                            } else clearListView();
                        }
                    }
                    if(sReviewList.isEmpty())   clearListView();
                } else clearListView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    void setUser(User user) { mUser = user; }

    private void clearListView() {
        sReviewList.clear();
        mReviewAdapter.clear();
        mReviewAdapter.notifyDataSetChanged();
        mImageView.setImageResource(R.mipmap.no_review);
        Toast.makeText(getContext(),"Sorry! No reviews to show", Toast.LENGTH_SHORT).show();
        mProgressBar.setVisibility(View.GONE);
    }

    private boolean isAdmin() {
        String userEmail = mUser.getUserEmail();
        for(String adminEmail: ADMIN_USERS) {
            if(adminEmail.equals(userEmail))    return true;
        }
        return false;
    }

    private void showReviews(Review currentReview) {
        if (!sReviewList.contains(currentReview)) {
            mImageView.setImageResource(R.mipmap.savereview);
            sReviewList.add(currentReview);
            mReviewAdapter.notifyDataSetChanged();
        } else {
            Review prevReview = mReviewAdapter.getItem(mReviewAdapter.getPosition(currentReview));
            if (prevReview != null && !currentReview.toString().
                    equalsIgnoreCase(prevReview.toString())) {
                sReviewList.remove(prevReview);
                mReviewAdapter.remove(prevReview);
                sReviewList.add(currentReview);
                mReviewAdapter.notifyDataSetChanged();
            }
        }
    }

    static ArrayList<Review> getReviewList() { return sReviewList; }
    static DatabaseReference getDatabaseReference() { return sMyRef; }
}
