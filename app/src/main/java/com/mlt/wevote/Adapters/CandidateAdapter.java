package com.mlt.wevote.Adapters;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mlt.wevote.Activities.VoteActivity;
import com.mlt.wevote.Models.CandidateModel;
import com.mlt.wevote.R;
import com.mlt.wevote.Singletons.Voter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CandidateAdapter extends RecyclerView.Adapter<CandidateAdapter.ViewHolder> {

    private List<CandidateModel> candidateModels;
    private Intent intent;

    private Voter voter;

    public CandidateAdapter(List<CandidateModel> candidateModels) {
        this.candidateModels = candidateModels;
        voter = Voter.getInstance();
    }

    @NonNull
    @Override
    public CandidateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        intent = new Intent(parent.getContext(), VoteActivity.class);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CandidateAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.name.setText(candidateModels.get(position).getName());
        holder.shortName.setText(candidateModels.get(position).getShortName());
        Glide.with(holder.itemView.getContext()).load(candidateModels.get(position).getSymbol()).into(holder.symbol);
        Glide.with(holder.itemView.getContext()).load(candidateModels.get(position).getPhoto()).into(holder.profilePhoto);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voter.setCandidateId(candidateModels.get(position).getCandidate());
                voter.setPartyId(String.valueOf(candidateModels.get(position).getParty()));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "Error getting bitmap", e);
        }
        return bm;
    }

    @Override
    public int getItemCount() {
        return candidateModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView shortName;
        private ImageView symbol;
        private CircleImageView profilePhoto;
        private TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            shortName = itemView.findViewById(R.id.short_name);
            symbol = itemView.findViewById(R.id.symbol);
            profilePhoto = itemView.findViewById(R.id.profile_image);
            name = itemView.findViewById(R.id.name);
        }
    }
}
