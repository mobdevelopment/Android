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
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

import org.imarks.vliegertest.R;
import org.imarks.vliegertest.adapter.SoundPoolManager;
import org.imarks.vliegertest.adapter.interfaces.ISoundPoolLoaded;
import org.imarks.vliegertest.model.ApiClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ListeningActivity extends AppCompatActivity {

    private boolean isPlaying = false;

    private int round = 0;
    private int score = 0;
    private boolean scoreModified = false;
    private boolean reachedLimit = false;

    private SharedPreferences settings;
    private volatile String lang = "en";

    private CardView cardView;

    private int[] hearedNumbers;

    private volatile Game game = new Game();

    private volatile SoundPoolManager soundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SoundPoolManager.CreateInstance();
        soundPool = SoundPoolManager.getInstance();
        loadSounds(lang);

        cardView = (CardView) findViewById(R.id.inputCard);

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        lang = settings.getString("listening_lang", "en");


        final AtomicBoolean started = new AtomicBoolean(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isPlaying) {
                    isPlaying = !isPlaying;
                    if (started.get() == false) {
                        started.set(true);
                        game.execute();
                    } else {
                        game.resume();
                    }
                } else {
                    isPlaying = !isPlaying;
                    game.pause();

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
        /*while(a < 5){
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            a++;
        }
        Log.e("loop", "started round: " + round);
        a = 0;*/
    }

    private Pair<Integer, String> getRandomSound(boolean allowNumber){
        double d = Math.random();
        Resources res = getResources();
        int sound;
        String choice;

        if (d < 0.2 && allowNumber) {
            choice = "" + randNumber();
            sound = res.getIdentifier(lang + Integer.parseInt(choice), "raw", getApplicationContext().getPackageName());
        } else {
            choice = "" + randChar();
            sound = res.getIdentifier(choice + lang, "raw", getApplicationContext().getPackageName());
        }

        return new Pair<>(sound, choice);
    }

    private static boolean checkMistakeMade(List<Integer> played, List<Integer> heared) {
        if (played.size() != heared.size()){
            return false;
        }

        Collections.sort(played);
        Collections.sort(heared);
        return !played.equals(heared); // If the array is the same no mistake is made, so revert the answer
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

    public void giveNumber(View v){
        int givenNumber = Integer.parseInt(v.getTag().toString());
        game.submitAnswer(givenNumber);
        Snackbar.make(v, "You answered with:" + givenNumber, Snackbar.LENGTH_LONG)
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

    private void loadSounds(String lang){
        String numbers = "0123456789";
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        List<Integer> sounds = new ArrayList<>();
        Resources res = getResources();

        sounds.add(res.getIdentifier("left", "raw", getApplicationContext().getPackageName()));
        sounds.add(res.getIdentifier("right", "raw", getApplicationContext().getPackageName()));
        sounds.add(res.getIdentifier("links", "raw", getApplicationContext().getPackageName()));
        sounds.add(res.getIdentifier("rechts", "raw", getApplicationContext().getPackageName()));

        String[] supportedLangs = { "en", "nl"};
        for (String ilang : supportedLangs){
            for (int i = 0; i < numbers.length() - 1; i++){
                sounds.add(res.getIdentifier(ilang + numbers.charAt(i), "raw", getApplicationContext().getPackageName()));
            }

            for (int i = 0; i < alphabet.length() - 1; i++){
                sounds.add(res.getIdentifier(alphabet.charAt(i) + ilang, "raw", getApplicationContext().getPackageName()));
            }
        }

        soundPool.setSounds(sounds);
        try {
            soundPool.InitializeSoundPool(this, new ISoundPoolLoaded() {
                @Override
                public void onSuccess() {
                    Toast.makeText(ListeningActivity.this, "Audio loaded", Toast.LENGTH_LONG);
                    Log.e("SoundPool", "Audio loaded");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*******************************************
     * Managing the game play/pause/stop state *
     *******************************************/

    private void activateGameLayout(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Drawable d = getResources().getDrawable(android.R.drawable.ic_media_pause);
        fab.setImageDrawable(d);

        CardView cardView = (CardView) findViewById(R.id.inputCard);
        cardView.setVisibility(View.VISIBLE);
        Log.e("LAYOUT", "Game layout enabled");
    }

    private void deactivateGameLayout(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Drawable d = getResources().getDrawable(android.R.drawable.ic_media_play);
        fab.setImageDrawable(d);

        CardView cardView = (CardView) findViewById(R.id.inputCard);
        cardView.setVisibility(View.GONE);
    }

    private void startTheGame() {
        activateGameLayout();

        playTheGame();
    }

    private void pauseTheGame() {
        deactivateGameLayout();
        game.pause();
    }

    private void stopTheGame() {
        pauseTheGame();
        game.cancel(true);
        game = new Game();
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
        game = new Game();

        super.onStop();
    }

    public void onResume() {
        if (score > 0 || round > 0) {
            isPlaying = true;
            startTheGame();
        }

        super.onResume();
    }

    /**********************************************************************************************
     * Game class                                                                                 *
     **********************************************************************************************/

    private class Game extends AsyncTask<Void, Void, String> {
        protected boolean isPaused = false;
        protected int round = 1;
        protected int score = 0;

        private String leaderboardKey = "CgkIgozg9vQREAIQAQ";

        protected List<Integer> hearedNumbers = new ArrayList<>();

        public void submitAnswer(int number){
            hearedNumbers.add(number);
            Log.v("Answering", "The answer " + number + " has being added to hearedNumbers");
        }

        protected void playRound(){
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Random r = new Random();
                    List<Integer> playedNumbers = new ArrayList<>(); // the max amount of numbers is 8 so we should do fine
                    // set array of answered numbers
                    hearedNumbers = new ArrayList<>();
                    int roundSize = r.nextInt((8 - 5) + 1) + 5; // Get a number in the range of 5 and 8 (inclusive)
                    boolean isLeft = r.nextBoolean();
                    Resources res = getResources();

                    int beep;
                    if (lang.equalsIgnoreCase("en")){
                        if (isLeft){
                            beep = res.getIdentifier("left", "raw", getApplicationContext().getPackageName());
                        } else {
                            beep = res.getIdentifier("right", "raw", getApplicationContext().getPackageName());
                        }
                    } else {
                        if (isLeft){
                            beep = res.getIdentifier("links", "raw", getApplicationContext().getPackageName());
                        } else {
                            beep = res.getIdentifier("rechts", "raw", getApplicationContext().getPackageName());
                        }
                    }

                    soundPool.playSound(beep);
                    sleep(3000);

                    while(playedNumbers.size() < roundSize){
                        while (isPaused)
                            sleep(1);

                        Pair<Integer, String> leftSoundData = getRandomSound(true);
                        Pair<Integer, String> rightSoundData = getRandomSound(true);
                        int soundLeft = leftSoundData.first;
                        int soundRight = rightSoundData.first;

                        if (isLeft && Character.isDigit(leftSoundData.second.charAt(0))){
                            playedNumbers.add(Integer.parseInt(leftSoundData.second));
                            Log.e("playedNumbers", playedNumbers.toString());
                        } else if (!isLeft && Character.isDigit(rightSoundData.second.charAt(0))) {
                            playedNumbers.add(Integer.parseInt(rightSoundData.second));
                            Log.e("playedNumbers", playedNumbers.toString());
                        }

                        soundPool.playSound(soundLeft, .99f, .0f);
                        soundPool.playSound(soundRight, .0f, .99f);
                        Log.e("Audio", "Left: " + soundLeft + " Right: " + soundRight);

                        sleep((int) Integer.parseInt(settings.getString("listening_pause_time", "1000")));
                    }

                    Log.e("round", "answer time enabled");
                    sleep(8000);

                    if (!checkMistakeMade(playedNumbers, hearedNumbers)){
                        score++;
                    }
                    Log.e("round", "ended round: " + round);
                }
            });

            t.start();

            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            activateGameLayout();
        }

        @Override
        protected String doInBackground(Void... params) {
            while (round < 24 && !isPaused){
                playRound();
                round++;
            }

            final GoogleApiClient apiClient = ApiClient.getInstance().getApiClient();
            if (apiClient.isConnected()){
                Games.Leaderboards.submitScore(ApiClient.getInstance().getApiClient(), leaderboardKey, score);
                Log.e("Leaderboards", "Added score of " + score + " in case 1");
            } else {
                final int fscore = score;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        apiClient.connect();
                        while (apiClient.isConnecting()){
                            // wait;
                        }
                        Games.Leaderboards.submitScore(ApiClient.getInstance().getApiClient(), leaderboardKey, fscore);
                        Log.e("Leaderboards", "Added score of " + fscore + " in case 2");
                    }
                }).start();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String aString){
            deactivateGameLayout();
            stopTheGame();
        }

        protected void pause()
        {
            this.isPaused = true;
            deactivateGameLayout();
        }

        protected void resume()
        {
            this.isPaused = false;
            activateGameLayout();
        }

        private void sleep(long sleepDuration){
            try {
                Thread.sleep(sleepDuration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
