package org.imarks.vliegertest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.imarks.vliegertest.R;
import org.imarks.vliegertest.adapter.ISRVAdapter;
import org.imarks.vliegertest.model.InstrumentScore;

import java.util.ArrayList;

public class InstrumentScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument_score);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        Intent intent = getIntent();
        ArrayList<InstrumentScore> questions = (ArrayList<InstrumentScore>) intent.getSerializableExtra("ScoreList");

        TextView score      = (TextView) findViewById(R.id.score);
        TextView maxScore   = (TextView) findViewById(R.id.maxScore);

        int iscore = 0;
        for (InstrumentScore question : questions ) {
            if (question.isCorrect) iscore++;
        }

        score.setText("" + iscore);
        maxScore.setText("" + questions.size());

        ISRVAdapter adapter = new ISRVAdapter(this, questions);
        rv.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
