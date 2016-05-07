package org.imarks.vliegertest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.imarks.vliegertest.R;
import org.imarks.vliegertest.adapter.ISRVAdapter;
import org.imarks.vliegertest.adapter.LSRVAdapter;
import org.imarks.vliegertest.model.InstrumentScore;
import org.imarks.vliegertest.model.ListeningScore;

import java.util.ArrayList;

public class ListeningScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening_score);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);

        Intent intent = getIntent();
        ArrayList<ListeningScore> rounds = (ArrayList<ListeningScore>) intent.getSerializableExtra("ScoreList");

        TextView score      = (TextView) findViewById(R.id.score);
        TextView maxScore   = (TextView) findViewById(R.id.maxScore);

        int iscore = 0;
        for (ListeningScore round : rounds ) {
            if (round.isCorrect) iscore++;
        }

        score.setText(String.format("%d", iscore));
        maxScore.setText(String.format("%d", rounds.size()));

        LSRVAdapter adapter = new LSRVAdapter(this, rounds);
        rv.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
