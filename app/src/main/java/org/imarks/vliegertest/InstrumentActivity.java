package org.imarks.vliegertest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class InstrumentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
}
