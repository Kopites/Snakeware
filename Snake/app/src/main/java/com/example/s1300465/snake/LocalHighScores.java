package com.example.s1300465.snake;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

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

        ArrayList<Score> scores = new DatabaseHelper(getContext()).getLocalScores();
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> scoreValues = new ArrayList<>();

        for(Score s : scores){
            names.add(s.getName());
            scoreValues.add(s.getScore());
        }

        String[] namesArray = names.toArray(new String[names.size()]);
        int[] scoreValuesArray = ((HighScoresActivity) getActivity()).convertIntArray(scoreValues);

        ScoresListViewAdapter adapter = new ScoresListViewAdapter(getActivity(), namesArray, scoreValuesArray);
        localScores.setAdapter(adapter);

        Log.d("Count", adapter.getCount() + "");

    }

}
