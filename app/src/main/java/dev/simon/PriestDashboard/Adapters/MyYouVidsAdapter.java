package dev.simon.PriestDashboard.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleRegistry;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.List;
import java.util.Observable;

import dev.simon.Classes.VidYou;
import dev.simon.Classes.Video;
import dev.simon.PriestDashboard.ViewActivities.PlayMyVideoActivity;
import dev.simon.PriestDashboard.ViewActivities.ViewMyYoutubeActivity;
import dev.simon.behold.R;

public class MyYouVidsAdapter extends RecyclerView.Adapter<MyYouVidsAdapter.YouViewHolder> {

    Context context;
    List<VidYou> myVidYou;

    public MyYouVidsAdapter(Context context, List<VidYou> myVidYou) {
        this.context = context;
        this.myVidYou = myVidYou;
    }


    @NonNull
    @Override
    public MyYouVidsAdapter.YouViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.you_layout, parent, false);

        return new YouViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyYouVidsAdapter.YouViewHolder holder, int position) {

        String id = myVidYou.get(position).getYou_id();

        getLifecycle().addObserver(holder.youtube_view);

        holder.youtube_des.setText(myVidYou.get(position).getYou_vid_des());
        holder.youtube_view.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                youTubePlayer.loadVideo(id, 0);

            }
        });

        holder.main_ll.setOnClickListener(View ->{
            Intent intent = new Intent(context, ViewMyYoutubeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("you_id", String.valueOf(myVidYou.get(position).getYou_vid_id()));
            bundle.putString("you_des", String.valueOf(myVidYou.get(position).getYou_vid_des()));
            bundle.putString("you_view_id", String.valueOf(myVidYou.get(position).getYou_id()));
            intent.putExtras(bundle);
            context.startActivity(intent);
        });

    }

    private Lifecycle getLifecycle() {
        return new LifecycleRegistry(this::getLifecycle);
    }

    @Override
    public int getItemCount() {
        return myVidYou.size();
    }

    public static class YouViewHolder extends RecyclerView.ViewHolder {

        private final TextView youtube_des;
        private final YouTubePlayerView youtube_view;
        private final LinearLayout main_ll;

        public YouViewHolder(@NonNull View itemView) {
            super(itemView);

            youtube_view =itemView.findViewById(R.id.youtube_view);
            youtube_des =itemView.findViewById(R.id.youtube_des);
            main_ll = itemView.findViewById(R.id.main_ll);

        }
    }
}
