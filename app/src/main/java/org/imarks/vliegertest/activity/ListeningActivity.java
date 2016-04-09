package org.imarks.vliegertest.activity;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import org.imarks.vliegertest.R;
import org.imarks.vliegertest.model.ApiClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ListeningActivity extends AppCompatActivity {

    private boolean isPlaying = false;

    private int round = 0;
    private int score = 0;
    private boolean scoreModified = false;
    private boolean reachedLimit = false;

    private SharedPreferences settings;
    private String lang = "en";

    private CardView cardView;

    private int[] hearedNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cardView = (CardView) findViewById(R.id.inputCard);

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        lang = settings.getString("listening_lang", "en");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPlaying) {
                    isPlaying = !isPlaying;
                    startTheGame();
                } else {
                    isPlaying = !isPlaying;
                    pauseTheGame();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * This method does nothing, it just describes the way the game is played
     *
     * @deprecated
     */
    private void description()
    {
        /**
         * @statics
         *
         * r: number of rounds: 24
         * n: amount of numbers: 5 - 8
         * m: allowed mistakes: 6 - 2
         * @TODO: suggestion, (m) is 6 in base, allow the user to set it to 2 to give correct feedback who failed already once in real life
         *
         * @text-description
         * The player gets to play (r) in which he must response with numbers.
         * each round start with a high or low tone, its defines to which side the user must listen
         * on this side the user needs to filter the heard numbers from the letters while the other side still plays different letters or numbers
         *
         * the players responses by clicking the correct number. the player is allowed to change the order of answering from what's heard
         * the goal of the test is only to test if the user can filter specific data. its thereby irrelevant to the order of answering
         *
         * @grading
         * during a round infinite mistakes can be made, it still count as one mistake
         * the total a allowed mistakes are depending if the user did the test before (in real life).
         * the first official attempt allows 6 mistakes, the second and also last official attempt allows 2
         *
         * @optional
         * - make (m) a setting to let the number of mistakes be bases on if he did the test before
         */
    }

    private void playTheGame(){
        //GoogleApiClient api = ApiClient.getInstance().getApiClient();
        //api.connect();
        round = 1;
        score = 0;

        while (round < 24 && isPlaying){
            playRound();
            round++;
        }

        Toast.makeText(ListeningActivity.this, "Ended the game in round " + round + " with " + score + " mistakes", Toast.LENGTH_SHORT).show();
        /*if (api.isConnected()){
            Games.Leaderboards.submitScore(ApiClient.getInstance().getApiClient(), "CgkIgozg9vQREAIQAQ", score);
            Toast.makeText(ListeningActivity.this, "Score submitted", Toast.LENGTH_SHORT).show();
        }*/
        stopTheGame();
    }

    private void playRound() {
        //new Thread(new Runnable() {
        //    @Override
        //    public void run() {
                int i = 0;
                while (i < 2) {
                    try {
                        Thread.sleep(1000);
                        i++;
                        score++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        //    }
        //}).start();
    }

    private static boolean checkMistakeMade(int[] played, int[] heared) {
        if (played.length != heared.length){
            return false;
        }

        Arrays.sort(played);
        Arrays.sort(heared);
        return !Arrays.equals(played, heared); // If the array is the same no mistake is made, so revert the answer
    }

    private void playSoundLeft(int id){
        playSound(1, 0, id);
    }

    private void playSoundRight(int id){
        playSound(0, 1, id);
    }

    private void playSound(int left, int right, int resource){
        MediaPlayer player = MediaPlayer.create(this, resource);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setVolume(left, right);
        player.start();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                mp.reset();
                //mp.stop();
            }
        });
    }

    public void giveNumber(View view){
        Object givenNumber = view.getTag();
        Snackbar.make(view, "You answered with:" + givenNumber.toString(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private int randNumber(){
        Random r = new Random();
        String options = "0123456789";

        int number = r.nextInt(options.length() - 1);
        return Character.getNumericValue(options.charAt(number));
    }

    private char randChar(){
        Random r = new Random();
        String letters = "abcdefghijklmnopqrstuvwxyz";

        int number = r.nextInt(letters.length() - 1);

        return letters.charAt(number);
    }

    /*******************************************
     * Managing the game play/pause/stop state *
     *******************************************/

    private void startTheGame() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Drawable d = getResources().getDrawable(android.R.drawable.ic_media_pause);
        fab.setImageDrawable(d);

        CardView cardView = (CardView) findViewById(R.id.inputCard);
        cardView.setVisibility(View.VISIBLE);

        playTheGame();
    }

    private void pauseTheGame() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Drawable d = getResources().getDrawable(android.R.drawable.ic_media_play);
        fab.setImageDrawable(d);

        CardView cardView = (CardView) findViewById(R.id.inputCard);
        cardView.setVisibility(View.GONE);
    }

    private void stopTheGame() {
        pauseTheGame();
        round = 0;
        score = 0;
        isPlaying = false;
    }

    /***********************************
     * Managing the activity lifeCycle *
     ***********************************/

    public void onPause(){
        isPlaying = false;
        pauseTheGame();

        super.onPause();
    }

    public void onStop() {
        isPlaying = false;
        stopTheGame();

        super.onStop();
    }

    public void onResume() {
        if (score > 0 || round > 0) {
            isPlaying = true;
            startTheGame();
        }

        super.onResume();
    }
}
