package dev.simon.UserDashboard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import dev.simon.Classes.Channel;
import dev.simon.Classes.Teach;
import dev.simon.UserDashboard.Activities.TeachViewActivity;
import dev.simon.behold.R;

public class TeachFragmentAdapter extends RecyclerView.Adapter<TeachFragmentAdapter.TeachFragViewHolder> {

    private Channel curr_channel;

    Context context;
    List<Teach> teachList;

    public TeachFragmentAdapter(Context context, List<Teach> teachList){
        this.context = context;
        this.teachList = teachList;
    }

    @NonNull
    @Override
    public TeachFragmentAdapter.TeachFragViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.teach_frag_layout, parent, false);
        return new TeachFragViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeachFragmentAdapter.TeachFragViewHolder holder, int position) {

        Picasso.get().load(teachList.get(position).getTeach_img()).placeholder(R.drawable.loading).into(holder.tea_frag_bg);
        holder.tea_frag_ds.setText(teachList.get(position).getTeach_des());
        holder.tea_frag_vrs.setText(teachList.get(position).getTeach_verses());
        holder.tea_frag_tp.setText(teachList.get(position).getTeach_topic());
        holder.tea_frag_sb.setText(teachList.get(position).getTeach_sub());
        holder.tea_frag_likes.setText(String.valueOf(teachList.get(position).getTeach_likes()));

        holder.tea_frag_rel.setOnClickListener(View->{
            openTea(position);
        });

        holder.tea_frag_bg.setOnClickListener(View->{
            openTea(position);
        });

    }

    private void openTea(int position) {
        Intent intent = new Intent(context, TeachViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("teach_clicked_id", teachList.get(position).getTeach_id());
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return teachList.size();
    }

    public static class TeachFragViewHolder extends RecyclerView.ViewHolder {

        private ImageView tea_frag_bg;
        private RelativeLayout tea_frag_rel;
        private TextView tea_frag_likes, tea_frag_ps, tea_frag_tp, tea_frag_sb, tea_frag_vrs, tea_frag_ds;

        public TeachFragViewHolder(@NonNull View itemView) {
            super(itemView);

            tea_frag_bg = itemView.findViewById(R.id.tea_frag_bg);
            tea_frag_rel = itemView.findViewById(R.id.tea_frag_rel);
            tea_frag_likes = itemView.findViewById(R.id.tea_frag_likes);
            tea_frag_ps = itemView.findViewById(R.id.tea_frag_ps);
            tea_frag_tp = itemView.findViewById(R.id.tea_frag_tp);
            tea_frag_sb = itemView.findViewById(R.id.tea_frag_sb);
            tea_frag_vrs = itemView.findViewById(R.id.tea_frag_vrs);
            tea_frag_ds = itemView.findViewById(R.id.tea_frag_ds);

        }
    }
}
