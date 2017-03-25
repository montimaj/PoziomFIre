package com.poziomlabs.firebasestore;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class DraftsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drafts, container, false);
        ListView listView = (ListView) view.findViewById(R.id.draftView);
        DraftsAdapter draftsAdapter = new DraftsAdapter(getActivity(), R.layout.content_draft,
                ReviewFragment.getDraftList());
        listView.setAdapter(draftsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                LocalReview review = (LocalReview) adapterView.getAdapter().getItem(i);
                ReviewFragment reviewFragment = new ReviewFragment();
                reviewFragment.setReview(review);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, reviewFragment)
                        .addToBackStack("Reviews")
                        .commit();
            }
        });

        return view;
    }
}
