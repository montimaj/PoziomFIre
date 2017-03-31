package com.poziomlabs.firebasestore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;

import static com.poziomlabs.firebasestore.ReviewFragment.setDraftList;

public class DraftsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drafts, container, false);
        ListView listView = (ListView) view.findViewById(R.id.draftView);
        ArrayList<LocalReview> drafts =  !ReviewFragment.getDraftList().isEmpty() ?
                ReviewFragment.getDraftList() : loadDrafts();
        if(!drafts.isEmpty()) {
            DraftsAdapter draftsAdapter = new DraftsAdapter(getActivity(),
                    R.layout.content_draft, drafts);
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
        } else {
            Toast.makeText(getContext(), "No reviews!", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }

        return view;
    }

    private ArrayList<LocalReview> loadDrafts() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Drafts", Context.MODE_PRIVATE);
        HashSet<String> draftSet = (HashSet<String>) sharedPreferences.getStringSet("Draft_Set", new HashSet<String>());
        ArrayList<LocalReview> drafts = new ArrayList<>();
        for (String gson : draftSet) {
            LocalReview review = new Gson().fromJson(gson, LocalReview.class);
            drafts.add(review);
        }
        setDraftList(drafts);
        return drafts;
    }
}
