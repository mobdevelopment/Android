package org.imarks.vliegertest.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.imarks.vliegertest.R;
import org.imarks.vliegertest.model.ListeningScore;

import java.util.List;

/**
 * Created by Mark on 19-4-2016.
 */
public class LSRVAdapter extends RecyclerView.Adapter<LSRVAdapter.ScoreViewHolder> {

    private Context context;

    List<ListeningScore> listeningScore;

    public LSRVAdapter(Context context, List<ListeningScore> listeningScore){
        this.context           = context;
        this.listeningScore    = listeningScore;
    }

    @Override
    public ScoreViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listening_scorecard, viewGroup, false);
        ScoreViewHolder svh = new ScoreViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(ScoreViewHolder holder, int i) {
        Resources res = context.getResources();
        holder.lcv.getBackground().setColorFilter(res.getColor(listeningScore.get(i).isCorrect ? R.color.correct : R.color.incorrect), PorterDuff.Mode.SRC_IN);
        holder.played.setText(TextUtils.join(",", listeningScore.get(i).played.toArray()));
        holder.heared.setText(TextUtils.join(",", listeningScore.get(i).heared.toArray()));
    }

    @Override
    public int getItemCount() {
        return listeningScore.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ScoreViewHolder extends RecyclerView.ViewHolder {

        CardView lcv;
        TextView played;
        TextView heared;

        ScoreViewHolder(View itemView){
            super(itemView);
            lcv              = (CardView) itemView.findViewById(R.id.lcv);
            played          = (TextView) itemView.findViewById(R.id.played);
            heared          = (TextView) itemView.findViewById(R.id.heared);
        }
    }
}
