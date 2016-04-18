package org.imarks.vliegertest.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mGoogleApiClient.isConnecting()){
                    // wait;
                }
                startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, "CgkIgozg9vQREAIQAg"), 1337);
            }
        }).start();
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!mGoogleApiClient.isConnected())
                    mGoogleApiClient.connect();

                while (mGoogleApiClient.isConnecting()){
                    // wait;
                }

                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, "CgkIgozg9vQREAIQAg"), 1337);
                }
            }
        }).start();
    }
}
