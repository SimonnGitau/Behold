package dev.simon.PriestDashboard.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.List;

import dev.simon.Classes.Teach;
import dev.simon.PriestDashboard.ViewActivities.PlayMyVideoActivity;
import dev.simon.PriestDashboard.ViewActivities.ViewMyTeaching;
import dev.simon.behold.R;

public class MyTeachingAdapter extends RecyclerView.Adapter<MyTeachingAdapter.MyTeachViewHolder> {

    Context context;
    List<Teach> myTeachList;

    public MyTeachingAdapter(Context context, List<Teach> myTeachList){
        this.context = context;
        this.myTeachList = myTeachList;
    }

    @NonNull
    @Override
    public MyTeachingAdapter.MyTeachViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_teach_layout, parent, false);
        return new MyTeachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTeachingAdapter.MyTeachViewHolder holder, int position) {

        holder.p_teach_topic.setText(myTeachList.get(position).getTeach_topic());
        holder.p_teach_sub.setText(myTeachList.get(position).getTeach_sub());
        holder.p_teach_des.setText(myTeachList.get(position).getTeach_des());
        holder.p_teach_likes.setText(myTeachList.get(position).getTeach_des());
        holder.p_teach_vs.setText(myTeachList.get(position).getTeach_verses());
        Picasso.get().load(myTeachList.get(position).getTeach_img()).placeholder(R.drawable.loading).into(holder.p_teach_img);
        holder.p_teach_likes.setText(String.format("%sk", String.valueOf(0)));

        holder.main_cv.setOnClickListener(View -> {
            Intent intent = new Intent(context, ViewMyTeaching.class);
            Bundle bundle = new Bundle();
            bundle.putString("tea_dp", myTeachList.get(position).getTeach_img());
            bundle.putString("tea_title", myTeachList.get(position).getTeach_topic());
            bundle.putString("tea_des", myTeachList.get(position).getTeach_des());
            bundle.putString("tea_verse", myTeachList.get(position).getTeach_verses());
            bundle.putString("tea_sub", myTeachList.get(position).getTeach_sub());
            bundle.putString("tea_id", myTeachList.get(position).getTeach_id());
            intent.putExtras(bundle);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return myTeachList.size();
    }

    public static class MyTeachViewHolder extends RecyclerView.ViewHolder {

        private final TextView p_teach_topic, p_teach_sub, p_teach_des, p_teach_likes, p_teach_vs;
        private final ImageView p_teach_img;
        private final MaterialCardView main_cv;

        public MyTeachViewHolder(@NonNull View itemView) {
            super(itemView);

            p_teach_topic = itemView.findViewById(R.id.p_teach_topic);
            p_teach_sub = itemView.findViewById(R.id.p_teach_sub);
            p_teach_des = itemView.findViewById(R.id.p_teach_des);
            p_teach_likes = itemView.findViewById(R.id.p_teach_likes);
            p_teach_vs = itemView.findViewById(R.id.p_teach_vs);
            p_teach_img = itemView.findViewById(R.id.p_teach_img);
            main_cv = itemView.findViewById(R.id.main_cv);

        }
    }
}
