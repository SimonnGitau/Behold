package dev.simon.PriestDashboard.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dev.simon.Classes.Video;
import dev.simon.PriestDashboard.ViewActivities.PlayMyVideoActivity;
import dev.simon.behold.R;

public class MyVideoAdapter extends RecyclerView.Adapter<MyVideoAdapter.MyVideosHolder> {

    Context context;
    List<Video> myVideosList;

    public MyVideoAdapter(Context context, List<Video> myVideosList) {
        this.context = context;
        this.myVideosList = myVideosList;
    }

    @NonNull
    @Override
    public MyVideoAdapter.MyVideosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_videos_layout, parent, false);
        return new MyVideosHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVideoAdapter.MyVideosHolder holder, int position) {

        holder.my_vid_title.setText(myVideosList.get(position).getVideo_title());
        holder.my_vid_time.setText(myVideosList.get(position).getVideo_upload_time());
        holder.my_vid_views.setText(String.valueOf(myVideosList.get(position).getVideo_views()));

        Glide.with(context).load(myVideosList.get(position).getVideo_url())
                .placeholder(R.drawable.loading)
                .override(2160, 1080)
                .fitCenter()
                .into(holder.my_video_view);


        holder.play_video.setOnClickListener(View ->{

            nextActivity(position);

        });

        holder.my_video_view.setOnClickListener(View ->{

           nextActivity(position);

        });

    }

    private void nextActivity(int position) {
        Intent intent = new Intent(context, PlayMyVideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("video_id", String.valueOf(myVideosList.get(position).getVideo_id()));
        bundle.putString("video_context", String.valueOf(myVideosList.get(position).getVideo_des()));
        bundle.putString("video_title", String.valueOf(myVideosList.get(position).getVideo_title()));
        bundle.putString("video_clicked_url", String.valueOf(myVideosList.get(position).getVideo_url()));
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return myVideosList.size();
    }

    public static class MyVideosHolder extends RecyclerView.ViewHolder {

        private ImageView my_video_view, play_video;
        private TextView my_vid_title, my_vid_time, my_vid_views;

        public MyVideosHolder(@NonNull View itemView) {
            super(itemView);

            my_vid_title = itemView.findViewById(R.id.my_vid_title);
            my_video_view = itemView.findViewById(R.id.my_video_view);
            my_vid_time = itemView.findViewById(R.id.my_vid_time);
            play_video = itemView.findViewById(R.id.play_video);
            my_vid_views = itemView.findViewById(R.id.my_vid_views);

        }
    }
}
