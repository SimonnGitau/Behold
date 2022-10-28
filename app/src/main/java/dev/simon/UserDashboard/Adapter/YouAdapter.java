package dev.simon.UserDashboard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;

import dev.simon.Classes.VidYou;
import dev.simon.UserDashboard.Activities.VideoPlayActivity;
import dev.simon.UserDashboard.Activities.YouFullActivity;
import dev.simon.behold.R;

public class YouAdapter extends RecyclerView.Adapter<YouAdapter.YouViewHolder> {

    Context context;
    List<VidYou> vidYous;

    public YouAdapter(Context context, List<VidYou> vidYous) {
        this.context = context;
        this.vidYous = vidYous;
    }

    @NonNull
    @Override
    public YouAdapter.YouViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.youtube_layout, parent, false);
        return new YouViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull YouAdapter.YouViewHolder holder, int position) {

        String id = vidYous.get(position).getYou_id();

        getLifecycle().addObserver(holder.you_view);

        holder.you_des.setText(vidYous.get(position).getYou_vid_des());

        holder.you_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                //youTubePlayer.loadVideo(id, 0);
                youTubePlayer.cueVideo(id, 0);
                youTubePlayer.pause();

            }
        });

        holder.view_full.setOnClickListener(View ->{

            Intent intent = new Intent(context, YouFullActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("video_id", id);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });

    }

    private Lifecycle getLifecycle() {
        return new LifecycleRegistry(this::getLifecycle);
    }

    @Override
    public int getItemCount() {
        return vidYous.size();
    }

    public static class YouViewHolder extends RecyclerView.ViewHolder {

        private YouTubePlayerView you_view;
        private TextView you_des;
        private MaterialButton view_full;

        public YouViewHolder(@NonNull View itemView) {
            super(itemView);

            you_view = itemView.findViewById(R.id.you_view);
            you_des = itemView.findViewById(R.id.you_des);
            view_full = itemView.findViewById(R.id.view_full);

        }
    }
}
