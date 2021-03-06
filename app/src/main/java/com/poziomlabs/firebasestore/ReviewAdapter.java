package com.poziomlabs.firebasestore;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

class ReviewAdapter extends ArrayAdapter<Review> {

    private Activity mActivity;
    private ArrayList<Review> mReviewList;
    private boolean mShowUserReviews;

    ReviewAdapter(Activity activity, int textViewResourceId, ArrayList<Review> arrayList) {
        super(activity, textViewResourceId, arrayList);
        mActivity = activity;
        mReviewList = arrayList;
    }

    private class ViewHolder {
        private TextView mTextView;
        private RatingBar mRatingBar;
    }

    @Override
    public int getCount() {
        super.getCount();
        return mShowUserReviews ? mReviewList.size() : MainFragment.getReviewList().size();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            convertView = layoutInflater.inflate(R.layout.content_review, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.review);
            viewHolder.mRatingBar = (RatingBar) convertView.findViewById(R.id.ratingBar2);
            viewHolder.mRatingBar.setIsIndicator(true);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ArrayList<Review> reviewList = mShowUserReviews ? mReviewList : MainFragment.getReviewList();
        if (!reviewList.isEmpty()) {
            Review review = reviewList.get(position);
            String s = review.getTitle();
            if(!review.getModeratorFlag())  s += "\nStatus: Pending";
            viewHolder.mTextView.setText(s);
            viewHolder.mRatingBar.setRating(review.getRating());
        }
        return convertView;
    }

    void setShowUserReviews() { mShowUserReviews = true; }
}
