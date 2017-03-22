package com.poziomlabs.firebasestore;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class CustomAdapter extends ArrayAdapter<Review> {

    private Activity mActivity;

    CustomAdapter(Activity activity, int textViewResourceId, ArrayList<Review> arrayList) {
        super(activity, textViewResourceId, arrayList);
        mActivity = activity;
    }

    private class ViewHolder {
        private TextView mTextView;
    }

    @Override
    public int getCount() {
        super.getCount();
        return MainActivity.getReviewList().size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            convertView = layoutInflater.inflate(R.layout.content_review, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.review);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArrayList<Review> reviewList = MainActivity.getReviewList();

        if(!reviewList.isEmpty()) {
            Review review = reviewList.get(position);
            viewHolder.mTextView.setText(review.toString());
        }
        return convertView;
    }
}
