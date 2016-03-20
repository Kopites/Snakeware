package com.example.s1300465.snake;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GlobalHighScores extends Fragment implements APIResponse{
    View rootView;

    public GlobalHighScores() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_global_high_scores, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;

        new ScoreFetcher(this).fetchScores();
    }

    @Override
    public void resultsReturned(JSONObject results){
        ListView globalScores = (ListView) rootView.findViewById(R.id.lstGlobalScores);

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> scoreValues = new ArrayList<>();

        try {
            for (int i = 0; i < results.names().length(); i++) {
                JSONObject score = (JSONObject) results.get(results.names().getString(i));
                names.add(score.getString("name"));
                scoreValues.add(score.getInt("score"));
            }
        }catch(JSONException ex){
            ex.printStackTrace();
        }

        String[] namesArray = names.toArray(new String[names.size()]);
        int[] scoreValuesArray = ((HighScoresActivity) getActivity()).convertIntArray(scoreValues);

        ScoresListViewAdapter adapter = new ScoresListViewAdapter(getActivity(), namesArray, scoreValuesArray);
        globalScores.setAdapter(adapter);

        Log.d("Count", adapter.getCount() + "");
    }
}
