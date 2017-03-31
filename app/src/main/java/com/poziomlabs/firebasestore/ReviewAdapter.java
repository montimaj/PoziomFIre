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

    ReviewAdapter(Activity activity, int textViewResourceId, ArrayList<Review> arrayList) {
        super(activity, textViewResourceId, arrayList);
        mActivity = activity;
    }

    private class ViewHolder {
        private TextView mTextView;
        private RatingBar mRatingBar;
    }

    @Override
    public int getCount() {
        super.getCount();
        return MainFragment.getReviewList().size();
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
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArrayList<Review> reviewList = MainFragment.getReviewList();

        if(!reviewList.isEmpty()) {
            Review review = reviewList.get(position);
            viewHolder.mTextView.setText(review.getTitle());
            viewHolder.mRatingBar.setRating(review.getRating());
        }
        return convertView;
    }
}
