package org.imarks.vliegertest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

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
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mGoogleApiClient.isConnecting()){
                    // wait;
                }
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, "CgkIgozg9vQREAIQAQ"), 1337);
            }
        }).start();
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

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!mGoogleApiClient.isConnected())
                    mGoogleApiClient.connect();

                while (mGoogleApiClient.isConnecting()){
                    // wait;
                }

                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()){
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, "CgkIgozg9vQREAIQAQ"), 1337);
                }
            }
        }).start();
    }
}
