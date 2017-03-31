package com.poziomlabs.firebasestore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;

public class ReviewFragment extends Fragment {
    private EditText mTitle;
    private EditText mBody1;
    private EditText mBody2;
    private EditText mBody3;
    private FloatingActionButton mFab;
    private RatingBar mRatingBar;
    private LocalReview mLocalReview;
    private SharedPreferences mSharedPrefs;
    private boolean mIsSaved;

    private static ArrayList<LocalReview> sDraftList = new ArrayList<>();
    private static final String[] KEY_SET = {"Title", "Body1", "Body2", "Body3", "Rating"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        mFab = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        mRatingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        mTitle = (EditText) view.findViewById(R.id.reviewTitle);
        mBody1 = (EditText) view.findViewById(R.id.reviewBody1);
        mBody2 = (EditText) view.findViewById(R.id.reviewBody2);
        mBody3 = (EditText) view.findViewById(R.id.reviewBody3);
        mSharedPrefs = getActivity().getSharedPreferences(mLocalReview.getReviewId(), Context.MODE_PRIVATE);

        mTitle.setText(mSharedPrefs.getString(KEY_SET[0],""));
        mBody1.setText(mSharedPrefs.getString(KEY_SET[1], ""));
        mBody2.setText(mSharedPrefs.getString(KEY_SET[2], ""));
        mBody3.setText(mSharedPrefs.getString(KEY_SET[3], ""));
        mRatingBar.setRating(mSharedPrefs.getFloat(KEY_SET[4], 0f));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mTitle.getText().toString();
                if(title.isEmpty()) {
                    Toast.makeText(getContext(), "Title should not be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    mLocalReview.setTitle(title);
                    mLocalReview.setBody(getReviewBody());
                    mLocalReview.setRating(mRatingBar.getRating());
                    saveReview();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        if(!mIsSaved) {
            String title = mTitle.getText().toString();
            float rating = mRatingBar.getRating();
            if(sDraftList.contains(mLocalReview)) {
                sDraftList.remove(mLocalReview);
            }
            mLocalReview.updateTime();
            mLocalReview.setTitle(title);
            mLocalReview.setBody(getReviewBody());
            mLocalReview.setRating(rating);
            sDraftList.add(mLocalReview);

            editor.putString(KEY_SET[0], title);
            editor.putString(KEY_SET[1], mBody1.getText().toString());
            editor.putString(KEY_SET[2], mBody2.getText().toString());
            editor.putString(KEY_SET[3], mBody3.getText().toString());
            editor.putFloat(KEY_SET[4], rating);
            editor.apply();
            saveDraft();
        } else {
            if(sDraftList.contains(mLocalReview))   sDraftList.remove(mLocalReview);
            editor.clear().apply();
            saveDraft();
        }
    }

    void setReview(LocalReview review) { mLocalReview = review; }

    private String getReviewBody() {
        String pros = mBody1.getText().toString();
        String cons = mBody2.getText().toString();
        String comments = mBody3.getText().toString();
        String body = "";
        if(!pros.isEmpty()) {
            body = "Pros: " + pros;
        }
        if(!cons.isEmpty()) {
            body += "\n\n" + "Cons: " + cons;
        }
        if(!comments.isEmpty()) {
            body += "\n\n" + "Other comments: " + comments;
        }
        return body;
    }

    static void setDraftList(ArrayList<LocalReview> drafts) { sDraftList = drafts; }
    static ArrayList<LocalReview> getDraftList() { return sDraftList; }

    private void saveReview() {
        ArrayList<String> wifis = mLocalReview.getSelectedWifis();
        Review review = new Review();
        review.setTitle(mLocalReview.getTitle());
        review.setBody(mLocalReview.getBody());
        review.setRating(mLocalReview.getRating());
        review.setModeratorFlag(false);
        review.setReviewId(mLocalReview.getReviewId());
        DatabaseReference ref = MainFragment.getDatabaseReference();
        for (String bssid : wifis) {
            ref.child(bssid).child(review.getReviewId()).setValue(review);
        }
        mIsSaved = true;
    }

    private void saveDraft() {
        mSharedPrefs = getActivity().getSharedPreferences("Drafts", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        HashSet<String> draftSet = new HashSet<>();
        for(LocalReview review: sDraftList) {
            String gson = new Gson().toJson(review);
            System.out.println(gson);
            draftSet.add(gson);
        }
        editor.putStringSet("Draft_Set", draftSet);
        editor.apply();
    }
}
