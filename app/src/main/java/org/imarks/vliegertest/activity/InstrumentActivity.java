package org.imarks.vliegertest.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.imarks.vliegertest.R;
import org.imarks.vliegertest.model.ApiClient;
import org.imarks.vliegertest.model.Question;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class InstrumentActivity extends AppCompatActivity {

    private List<Question> questions;
    private Question current;

    private ImageView altitude, compass, optionA, optionB, optionC, optionD;

    private final int answerTint = Color.argb(204, 204, 204, 204);

    private static GoogleApiClient apiClient = ApiClient.getInstance().getApiClient();
    protected static String leaderboardKey = "CgkIgozg9vQREAIQAg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!apiClient.isConnected() && !apiClient.isConnecting()){
            apiClient.connect();
        }

        altitude    = (ImageView) findViewById(R.id.altitude);
        compass     = (ImageView) findViewById(R.id.compass);
        optionA     = (ImageButton) findViewById(R.id.image1);
        optionB     = (ImageButton) findViewById(R.id.image2);
        optionC     = (ImageButton) findViewById(R.id.image3);
        optionD     = (ImageButton) findViewById(R.id.image4);

        QuestionFetcher fetcher = new QuestionFetcher();
        fetcher.execute();

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
         * q: number of questions: 60
         * t: time available: 20
         * @TODO: suggestion, make this a setting. it will be useful for grading
         *
         * @text-description
         * The game has a simple setup, during the available (t) the user must answer (q)
         * each (q) exists of 2 images display the actual status of the plane
         * the first image is showing climb and tilt of the plane
         * the second image is showing the compass
         *
         * with this information you need to pick the right plane shown out of 4 images
         * each plane has its own climb, tilt and direction.
         *
         * @grading
         * player starts with a ten, each mistake a point degrading, minimal score is 5
         * the player gets no feedback on his score during the game.
         * after answering the user goes to the next (q).
         *
         * @optional
         * - make statics settings
         * - allow the user to move a (q) back, to correct a given answer
         *      - Make a setting to move beck infinite steps or 1 step.
         */
    }

    private void handleQuestionsList(List<Question> questions) {
        Collections.shuffle(questions, new Random(System.nanoTime()));
        this.questions = questions;

        current = this.questions.get(0);

        changeImages(current);
    }

    public void giveAnswer(View v) {
        ImageButton image = (ImageButton) v;

        optionA.setColorFilter(null);
        optionB.setColorFilter(null);
        optionC.setColorFilter(null);
        optionD.setColorFilter(null);

        image.setColorFilter(answerTint);
        int option = Integer.parseInt(v.getTag().toString());
        current.answer = option;
    }

    public void previous(View v) {
        int index = questions.indexOf(current);
        Log.e("index", "" + index);
        if (index > 0){
            Question previous = questions.get(index - 1);
            changeImages(previous);
        }
    }


    public void next(View v) {
        int index = questions.indexOf(current);
        Log.e("Navigation", "Index: " + index + " Size: " + questions.size());
        if (index < questions.size() - 1 && current.answer > 0){
            Question next = questions.get(index + 1);
            changeImages(next);
        } else if (current.answer > 0) {
            Finished();
        }
    }

    private void Finished(){
        int score = 0;
        for (Question question : questions) {
            if (question.images.get(question.answer - 1).correct){
                score++;
            }
        }

        if (!apiClient.isConnected())
            apiClient.connect();

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

    private void changeImages(Question question){
        current = question;

        optionA.setColorFilter(null);
        optionB.setColorFilter(null);
        optionC.setColorFilter(null);
        optionD.setColorFilter(null);

        new DownloadImageTask(altitude).execute(question.altitude.path);
        new DownloadImageTask(compass).execute(question.compass.path);
        new DownloadImageTask(optionA).execute(question.images.get(0).path);
        new DownloadImageTask(optionB).execute(question.images.get(1).path);
        new DownloadImageTask(optionC).execute(question.images.get(2).path);
        new DownloadImageTask(optionD).execute(question.images.get(3).path);

        if (question.answer > 0){
            switch (question.answer){
                case 1:
                    optionA.setColorFilter(answerTint);
                    break;
                case 2:
                    optionB.setColorFilter(answerTint);
                    break;
                case 3:
                    optionC.setColorFilter(answerTint);
                    break;
                case 4:
                    optionD.setColorFilter(answerTint);
                    break;
                default:
                    break;
            }
        }
    }

    private void failedLoadingQuestions() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(InstrumentActivity.this, "Failed to load questions", Toast.LENGTH_SHORT);
            }
        });
    }

    private class QuestionFetcher extends AsyncTask<Void, Void, String> {
        private static final String TAG = "QuestionFetcher";
        private StringBuffer chain = new StringBuffer();

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL("http://imarks.org/api/question");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "");
                connection.setRequestMethod("GET");
                connection.setDoOutput(false);
                connection.connect();

                InputStream inputStream = connection.getInputStream();

                try {
                    Reader reader = new InputStreamReader(inputStream);

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    List<Question> questions = Arrays.asList(gson.fromJson(reader, Question[].class));

                    handleQuestionsList(questions);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse JSON due to: " + e);
                    failedLoadingQuestions();
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to send HTTP GET request ");
            }
            return null;
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream inputStream = new URL(urldisplay).openStream();
                mIcon = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                Log.e("Image Download", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            ViewGroup.LayoutParams params = bmImage.getLayoutParams();
            params.height = 360;
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            bmImage.setImageBitmap(Bitmap.createScaledBitmap(result, 360, 360, false));
            bmImage.setLayoutParams(params);
            bmImage.requestLayout();
        }
    }
}
