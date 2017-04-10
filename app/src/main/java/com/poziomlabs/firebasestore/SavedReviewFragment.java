package com.poziomlabs.firebasestore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;

import static com.poziomlabs.firebasestore.ReviewFragment.setSavedReviewList;

public class SavedReviewFragment extends Fragment {
    private User mUser;
    private AlertDialog mAlertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savedreviews, container, false);
        ArrayList<Review> myReviews =  !ReviewFragment.getSavedReviewList().isEmpty() ?
                ReviewFragment.getSavedReviewList() : loadSavedReviews();
        if(!myReviews.isEmpty()) {
            ReviewAdapter reviewAdapter = new ReviewAdapter(getActivity(), R.layout.content_review, myReviews);
            reviewAdapter.setShowUserReviews();
            ListView listView = (ListView) view.findViewById(R.id.listView1);
            listView.setAdapter(reviewAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Review review = (Review) adapterView.getAdapter().getItem(i);
                    displayReviewDialog(review);
                }
            });
        } else {
            Toast.makeText(getContext(), "No saved reviews!", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }
        return view;
    }

    private ArrayList<Review> loadSavedReviews() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Saved", Context.MODE_PRIVATE);
        HashSet<String> reviewSet = (HashSet<String>) sharedPreferences.getStringSet("Review_Set", new HashSet<String>());
        ArrayList<Review> reviews = new ArrayList<>();
        if(!reviewSet.isEmpty()) {
            for (String gson : reviewSet) {
                Review review = new Gson().fromJson(gson, Review.class);
                reviews.add(review);
            }
        } else {
            ArrayList<Review> allReviews = MainFragment.getReviewList();
            for(Review review: allReviews) {
                if(review.getUser().equals(mUser)) {
                    reviews.add(review);
                }
            }
        }
        setSavedReviewList(reviews);
        return reviews;
    }

    private void displayReviewDialog(final Review review) {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View dialogView = layoutInflater.inflate(R.layout.content_savedreviews, null);
        mAlertDialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setTitle("Review Description")
                .setMessage(review.toString())
                .setPositiveButton("OK", null)
                .setCancelable(true)
                .create();

        RatingBar ratingBar = (RatingBar) dialogView.findViewById(R.id.ratingBar4);
        ratingBar.setRating(review.getRating());
        ratingBar.setIsIndicator(true);

        mAlertDialog.show();
        mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialog.dismiss();
            }
        });
    }

    void setUser(User user) { mUser = user; }
}
