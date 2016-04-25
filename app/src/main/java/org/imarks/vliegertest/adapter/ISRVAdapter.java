package org.imarks.vliegertest.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.imarks.vliegertest.DownloadImageTask;
import org.imarks.vliegertest.R;
import org.imarks.vliegertest.model.InstrumentScore;

import java.util.List;

/**
 * Created by Mark on 19-4-2016.
 */
public class ISRVAdapter extends RecyclerView.Adapter<ISRVAdapter.ScoreViewHolder> {

    private Context context;

    List<InstrumentScore> instrumentScore;

    public ISRVAdapter(Context context, List<InstrumentScore> instrumentScore){
        this.context            = context;
        this.instrumentScore    = instrumentScore;
    }

    @Override
    public ScoreViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.instrument_scorecard, viewGroup, false);
        ScoreViewHolder svh = new ScoreViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(ScoreViewHolder holder, int i) {
        Resources res = context.getResources();
        holder.cv.getBackground().setColorFilter(res.getColor(instrumentScore.get(i).isCorrect ? R.color.correct : R.color.incorrect), PorterDuff.Mode.SRC_IN);
        new DownloadImageTask(holder.altitude).execute(instrumentScore.get(i).altitude.path);
        new DownloadImageTask(holder.compass).execute(instrumentScore.get(i).compass.path);
        new DownloadImageTask(holder.correctImage).execute(instrumentScore.get(i).correct.path);
        new DownloadImageTask(holder.givenImage).execute(instrumentScore.get(i).given.path);
    }

    @Override
    public int getItemCount() {
        return instrumentScore.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ScoreViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        ImageView altitude;
        ImageView compass;
        ImageView correctImage;
        ImageView givenImage;

        ScoreViewHolder(View itemView){
            super(itemView);
            cv              = (CardView) itemView.findViewById(R.id.cv);
            altitude        = (ImageView) itemView.findViewById(R.id.altitude);
            compass         = (ImageView) itemView.findViewById(R.id.compass);
            correctImage    = (ImageView) itemView.findViewById(R.id.correctImage);
            givenImage      = (ImageView) itemView.findViewById(R.id.givenImage);
        }
    }
}
