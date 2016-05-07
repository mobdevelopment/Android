package org.imarks.vliegertest.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.BottomBarFragment;

import org.imarks.vliegertest.R;
import org.imarks.vliegertest.fragment.InstrumentScoreFragment;
import org.imarks.vliegertest.fragment.ListeningScoreFragment;
import org.imarks.vliegertest.model.ApiClient;

public class HighscoreActivity extends AppCompatActivity {

    private BottomBar mBottomBar;

    private static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.setFragmentItems(getSupportFragmentManager(), R.id.fragmentContainer,
                new BottomBarFragment(InstrumentScoreFragment.newInstance(), R.drawable.ic_menu_gallery, "Instrumententest"),
                new BottomBarFragment(ListeningScoreFragment.newInstance(), R.drawable.ic_media_play, "Luistertest")
        );*/

        mGoogleApiClient = ApiClient.getInstance().getApiClient();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!mGoogleApiClient.isConnected())
                    mGoogleApiClient.connect();

                while (mGoogleApiClient.isConnecting()){
                    // wait;
                }

                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(mGoogleApiClient), 1337);
                    //startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, "CgkIgozg9vQREAIQAg"), 1337);
                }
            }
        }).start();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        //mBottomBar.onSaveInstanceState(outState);
    }
}
