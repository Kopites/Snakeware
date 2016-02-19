package com.example.s1300465.snake;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class LocalHighScores extends Fragment {

    public LocalHighScores() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_high_scores, container, false);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ListView localScores = (ListView) view.findViewById(R.id.lstLocalScores);
        ScoresListViewAdapter adapter = new ScoresListViewAdapter(getActivity(), new String[]{"AAA", "BBB", "Butts"}, new int[]{12, 8, 5});
        localScores.setAdapter(adapter);

        Log.d("Count", adapter.getCount() + "");

    }

}
