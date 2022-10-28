package dev.simon.UserDashboard.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Channel;
import dev.simon.Classes.Video;
import dev.simon.UserDashboard.Activities.TeachViewActivity;
import dev.simon.UserDashboard.Activities.VideoPlayActivity;
import dev.simon.UserDashboard.Payments.PurchaseVideoActivity;
import dev.simon.behold.R;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {

    private FirebaseFirestore db;
    
    DatabaseReference myRef;
    FirebaseDatabase database;

    Video current_video;

    Context context;
    private List<Video> videos;

    public VideosAdapter(Context context, List<Video> videos) {
        this.context = context;
        this.videos = videos;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.videos_layout, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {

        db = FirebaseFirestore.getInstance();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("videos");

        holder.video_title.setText(videos.get(position).getVideo_title());
        holder.video_views.setText(String.valueOf(videos.get(position).getVideo_views()));
        holder.video_uptime.setText(videos.get(position).getVideo_upload_time());
        holder.txt_des.setText(videos.get(position).getVideo_des());
        holder.video_cost.setText(String.valueOf(videos.get(position).getVideo_cost()));

        Glide.with(context).load(videos.get(position).getVideo_url())
                .placeholder(R.drawable.loading)
                .override(2160, 1080)
                .fitCenter()
                .into(holder.thumb_nail);

        checkState(holder, position);

        holder.purchase_vid.setOnClickListener(View->{
            purchaseVideo(position);
        });

        holder.play_video.setOnClickListener(View->{
            playVideo(holder,position);
        });

    }

    private void checkState(VideoViewHolder holder, int position) {
        myRef.child(videos.get(position).getVideo_id()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                current_video = dataSnapshot.getValue(Video.class);
                assert current_video != null;
                if (!current_video.isVideo_purchased()){
                    checkPayed(holder, position);
                }else{
                    holder.purchase_vid.setVisibility(View.GONE);
                    holder.play_video.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void checkPayed(VideoViewHolder holder, int position) {
        db.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("videos_purchased")
                .document(videos.get(position).getVideo_id())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            holder.purchase_vid.setVisibility(View.GONE);
                            holder.play_video.setVisibility(View.VISIBLE);
                        }else {
                            holder.purchase_vid.setVisibility(View.VISIBLE);
                            holder.play_video.setVisibility(View.GONE);
                        }
                    }
                });

    }

    private void purchaseVideo(int position) {
        Intent intent = new Intent(context, PurchaseVideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("video_clicked_url", videos.get(position).getVideo_url());
        bundle.putString("video_clicked_id", videos.get(position).getVideo_id());
        bundle.putString("video_clicked_title", videos.get(position).getVideo_title());
        bundle.putString("video_cost", String.valueOf(videos.get(position).getVideo_cost()));
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private void playVideo(VideoViewHolder holder, int position) {
        
        Intent intent = new Intent(context, VideoPlayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("video_clicked_url", videos.get(position).getVideo_url());
        bundle.putString("video_clicked_id", videos.get(position).getVideo_id());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {

        private final TextView txt_des, video_uptime, video_title, video_views, video_cost;
        private final ImageView thumb_nail, play_video;
        private final LinearLayout purchase_vid;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_des = itemView.findViewById(R.id.txt_des);
            video_uptime = itemView.findViewById(R.id.video_uptime);
            video_title = itemView.findViewById(R.id.video_title);
            video_views = itemView.findViewById(R.id.video_views);
            thumb_nail = itemView.findViewById(R.id.thumb_nail);
            play_video = itemView.findViewById(R.id.ad_play_video);
            purchase_vid = itemView.findViewById(R.id.purchase_vid);
            video_cost = itemView.findViewById(R.id.video_cost);

        }
    }
}
