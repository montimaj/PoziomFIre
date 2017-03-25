package com.poziomlabs.firebasestore;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DraftsAdapter extends ArrayAdapter<LocalReview> {
    private Activity mActivity;

    DraftsAdapter(Activity activity, int textViewResourceId, ArrayList<LocalReview> arrayList) {
        super(activity, textViewResourceId, arrayList);
        mActivity = activity;
    }

    private class ViewHolder {
        private TextView mTextView;
    }

    @Override
    public int getCount() {
        super.getCount();
        return ReviewFragment.getDraftList().size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            convertView = layoutInflater.inflate(R.layout.content_draft, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.draft);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ArrayList<LocalReview> reviewList = ReviewFragment.getDraftList();

        if(!reviewList.isEmpty()) {
            LocalReview review = reviewList.get(position);
            viewHolder.mTextView.setText(review.toString());
        }
        return convertView;
    }
}
