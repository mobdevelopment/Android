package org.imarks.vliegertest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.imarks.vliegertest.R;
import org.imarks.vliegertest.model.ApiClient;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class InstrumentScoreFragment extends Fragment {

    private static GoogleApiClient mGoogleApiClient;

    public InstrumentScoreFragment() {

    }

    @Override
    public void onStart(){
        super.onStart();
        mGoogleApiClient.connect();

        if (mGoogleApiClient.isConnected()){
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, "CgkIgozg9vQREAIQAg"), 1337);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = ApiClient.getInstance().getApiClient();
    }

    public static InstrumentScoreFragment newInstance() {
        InstrumentScoreFragment instrumentScoreFragment = new InstrumentScoreFragment();
        return instrumentScoreFragment;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, "CgkIgozg9vQREAIQAg"), 1337);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instrument_score, container, false);
    }
}
