package org.imarks.vliegertest;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ListeningActivity extends AppCompatActivity {

    private boolean isPlaying = false;

    private int round = 0;
    private int score = 0;
    private boolean scoreModified = false;
    private boolean reachedLimit = false;

    private SharedPreferences settings = null;
    private String lang = "en";

    private List<Integer> numberStack = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    private void playTheGame(){
        round = 1;

        while (round < 2 && isPlaying){
            playRound();
            round++;
        }

        Toast.makeText(ListeningActivity.this, "Ended the game in round " + round + " with " + score + " mistakes", Toast.LENGTH_SHORT).show();
        stopTheGame();
    }

    private void playRound(){
        int numbers = 0;
        scoreModified = false;
        reachedLimit = false;

        while (isPlaying && !reachedLimit)
        {
            numbers++;

            Double chance = Math.random();
            if (chance < 0.0125 && !scoreModified) {
                score = 1;
                scoreModified = true;
            }

            if (numbers >= 8) {
                reachedLimit = true;
            }
        }

        return;
    }

    private void playTheGame2(){
        round = 1;

        Random r = new Random();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                    int numbers = 0;
                    while (isPlaying) {
                        double d = Math.random();
                        Resources res = getResources();
                        int soundLeft = 0;
                        int soundRight = 0;

                        // TODO: Make it possible to have a number left and right at the same time
                        if (d < 0.2) {
                            numbers++;
                            double e = Math.random();
                            if (e < 0.5){
                                // left number
                                // right letter
                                soundLeft   = res.getIdentifier(lang + randNumber(), "raw", getPackageName());
                                soundRight  = res.getIdentifier(randChar() + lang, "raw", getPackageName());
                            } else {
                                // left letter
                                // right number
                                soundLeft   = res.getIdentifier(randChar() + lang, "raw", getPackageName());
                                soundRight  = res.getIdentifier(lang + randNumber(), "raw", getPackageName());
                            }
                        } else {
                            // left letter
                            // right letter
                            soundLeft   = res.getIdentifier(randChar() + lang, "raw", getPackageName());
                            soundRight  = res.getIdentifier(randChar() + lang, "raw", getPackageName());
                        }

                        playSoundLeft(soundLeft);
                        playSoundRight(soundRight);

                        Thread.sleep((int) Integer.parseInt(settings.getString("listening_pause_time", "1000")));

                        // TODO: predefine the numbers in the round and make it possible to sum up so letters after finishin (or numbers on the 'wrong' side) to make the last number less hearable
                        // TODO: Make the call to start/pause/stop not within the thread or find a workaround
                        if (numbers > 5 && numbers < 8){
                            Double chance = Math.random();
                            if (chance < 0.33){
                                round++;
                                Toast.makeText(ListeningActivity.this, "Next round started", Toast.LENGTH_SHORT).show();

                                if (round == 2){
                                    isPlaying = false;
                                    stopTheGame();
                                }
                                numbers = 0;
                            }
                        } else if (numbers == 8){
                            round++;
                            if (round == 2){
                                isPlaying = false;
                                stopTheGame();
                            }
                            numbers = 0;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    Log.e("Settings", "Arr: " + settings.toString());
                    e.printStackTrace();
                }
            }
        }).start();

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
                mp.stop();
                mp.release();
            }

            ;
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

        CardView cardView = (CardView) findViewById(R.id.ll_reveal);
        cardView.setVisibility(View.VISIBLE);

        playTheGame();
    }

    private void pauseTheGame() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Drawable d = getResources().getDrawable(android.R.drawable.ic_media_play);
        fab.setImageDrawable(d);

        CardView cardView = (CardView) findViewById(R.id.ll_reveal);
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
