package dev.simon.UserDashboard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.simon.Classes.Channel;
import dev.simon.Classes.Teach;
import dev.simon.UserDashboard.Activities.EventViewActivity;
import dev.simon.UserDashboard.Activities.TeachViewActivity;
import dev.simon.behold.R;

public class TeachingsAdapter extends RecyclerView.Adapter<TeachingsAdapter.TeachingsViewHolder> {

    private Channel curr_channel;

    Context context;
    List<Teach> teachList;

    public TeachingsAdapter(Context context, List<Teach> teachList){
        this.context = context;
        this.teachList = teachList;
    }

    @NonNull
    @Override
    public TeachingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.teachings_layout, parent, false);
        return new TeachingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeachingsViewHolder holder, int position) {

        Picasso.get().load(teachList.get(position).getTeach_img()).placeholder(R.drawable.loading).into(holder.tea_bg);
        holder.teach_ds.setText(teachList.get(position).getTeach_des());
        holder.teach_vrs.setText(teachList.get(position).getTeach_verses());
        holder.teach_tp.setText(teachList.get(position).getTeach_topic());
        holder.teach_sb.setText(teachList.get(position).getTeach_sub());
        holder.teach_likes.setText(String.valueOf(teachList.get(position).getTeach_likes()));

        holder.view_rel.setOnClickListener(View->{
            openTea(position);
        });


        holder.teach_ds.setOnClickListener(View->{
           openTea(position);
        });

        holder.tea_bg.setOnClickListener(View->{
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
        return 5;
    }

    public static class TeachingsViewHolder extends RecyclerView.ViewHolder {

        private final ImageView tea_bg, tea_love;
        private final TextView teach_ps, teach_tp, teach_sb, teach_vrs, teach_ds, teach_likes;
        private final RelativeLayout view_rel;

        public TeachingsViewHolder(@NonNull View itemView) {
            super(itemView);

            tea_bg = itemView.findViewById(R.id.tea_bg);
            teach_ps = itemView.findViewById(R.id.teach_ps);
            teach_tp = itemView.findViewById(R.id.teach_tp);
            teach_sb = itemView.findViewById(R.id.teach_sb);
            teach_vrs = itemView.findViewById(R.id.teach_vrs);
            teach_ds = itemView.findViewById(R.id.teach_ds);
            teach_likes = itemView.findViewById(R.id.teach_likes);
            tea_love = itemView.findViewById(R.id.tea_love);
            view_rel = itemView.findViewById(R.id.view_rel);

        }
    }
}
