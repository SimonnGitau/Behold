package dev.simon.UserDashboard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

import dev.simon.Classes.Video;
import dev.simon.UserDashboard.Activities.VideoPlayActivity;
import dev.simon.UserDashboard.Payments.PurchaseVideoActivity;
import dev.simon.behold.R;

public class VideosFragmentAdapter extends RecyclerView.Adapter<VideosFragmentAdapter.VideosFragmentViewHolder> {

    private FirebaseFirestore db;

    private DatabaseReference myRef;
    private FirebaseDatabase database;

    private Video current_video;
    private String video_id;

    private Context context;
    private List<Video> videosList;

    public VideosFragmentAdapter(Context context, List<Video> videosList) {
        this.context = context;
        this.videosList = videosList;
    }

    @NonNull
    @Override
    public VideosFragmentAdapter.VideosFragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.videos_fragment_layout, parent, false);
        return new VideosFragmentAdapter.VideosFragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideosFragmentAdapter.VideosFragmentViewHolder holder, int position) {

        db = FirebaseFirestore.getInstance();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("videos");

        holder.frag_title.setText(videosList.get(position).getVideo_title());
        holder.frag_views.setText(String.valueOf(videosList.get(position).getVideo_views()));
        holder.frag_uptime.setText(videosList.get(position).getVideo_upload_time());
        holder.frag_cost.setText(String.valueOf(videosList.get(position).getVideo_cost()));

        video_id = videosList.get(position).getVideo_id();

        Glide.with(context).load(videosList.get(position).getVideo_url())
                .placeholder(R.drawable.loading)
                .override(2160, 1080)
                .fitCenter()
                .into(holder.frag_nail);

        checkState(holder, position, video_id);

        holder.frag_purchase.setOnClickListener(View->{
            purchaseVideo(position);
        });

        holder.frag_play.setOnClickListener(View->{
            playVideo(holder,position);
        });

    }

    private void checkState(VideosFragmentViewHolder holder, int position, String video_id) {
        myRef.child(videosList.get(position).getVideo_id()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                current_video = dataSnapshot.getValue(Video.class);
                assert current_video != null;
                if (current_video.isVideo_purchased()){
                    holder.frag_purchase.setVisibility(View.GONE);
                    holder.frag_play.setVisibility(View.VISIBLE);
                }else{
                    checkPayed(holder, position, video_id);
                }
            }
        });
    }

    private void purchaseVideo(int position) {

        Intent intent = new Intent(context, PurchaseVideoActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("video_clicked_url", videosList.get(position).getVideo_url());
        bundle.putString("video_clicked_id", videosList.get(position).getVideo_id());
        bundle.putString("video_clicked_title", videosList.get(position).getVideo_title());
        bundle.putString("video_cost", String.valueOf(videosList.get(position).getVideo_cost()));
        intent.putExtras(bundle);
        context.startActivity(intent);

    }

    private void playVideo(VideosFragmentViewHolder holder, int position) {

        Toast.makeText(context, videosList.get(position).getVideo_id(), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(context, VideoPlayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("video_clicked_id", videosList.get(position).getVideo_id());
        bundle.putString("video_clicked_url", videosList.get(position).getVideo_url());
        intent.putExtras(bundle);
        context.startActivity(intent);

    }

    private void checkPayed(VideosFragmentViewHolder holder, int position, String video_id) {

        db.collection("Users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("videos_purchased")
                .document(video_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()){
                            holder.frag_purchase.setVisibility(View.GONE);
                            holder.frag_play.setVisibility(View.VISIBLE);
                        }else {
                            holder.frag_purchase.setVisibility(View.VISIBLE);
                            holder.frag_play.setVisibility(View.GONE);
                        }
                    }
                });

    }

    @Override
    public int getItemCount() {
        return videosList.size();
    }

    public static class VideosFragmentViewHolder extends RecyclerView.ViewHolder {

        private final TextView frag_cost, frag_views, frag_uptime, frag_title;
        private final ImageView frag_nail, frag_play;
        private final LinearLayout frag_purchase;

        public VideosFragmentViewHolder(@NonNull View itemView) {
            super(itemView);

            frag_cost = itemView.findViewById(R.id.frag_cost);
            frag_views = itemView.findViewById(R.id.frag_views);
            frag_uptime = itemView.findViewById(R.id.frag_uptime);
            frag_title = itemView.findViewById(R.id.frag_title);
            frag_nail = itemView.findViewById(R.id.frag_nail);
            frag_play = itemView.findViewById(R.id.frag_play);
            frag_purchase = itemView.findViewById(R.id.frag_purchase);

        }
    }
}
