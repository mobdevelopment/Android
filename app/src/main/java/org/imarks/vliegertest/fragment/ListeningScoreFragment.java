package org.imarks.vliegertest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import org.imarks.vliegertest.R;
import org.imarks.vliegertest.model.ApiClient;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class ListeningScoreFragment extends Fragment {

    private static GoogleApiClient mGoogleApiClient;

    public ListeningScoreFragment() {

    }

    @Override
    public void onStart(){
        super.onStart();

        if (mGoogleApiClient.isConnected()){
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, "CgkIgozg9vQREAIQAQ"), 1337);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = ApiClient.getInstance().getApiClient();
    }

    public static ListeningScoreFragment newInstance() {
        ListeningScoreFragment instrumentScoreFragment = new ListeningScoreFragment();
        return instrumentScoreFragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instrument_score, container, false);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, "CgkIgozg9vQREAIQAQ"), 1337);
        }
    }
}
