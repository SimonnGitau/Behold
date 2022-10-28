package dev.simon.UserDashboard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.simon.Classes.Channel;
import dev.simon.UserDashboard.Activities.PriestProfileActivity;
import dev.simon.UserDashboard.Activities.TeachViewActivity;
import dev.simon.behold.MainActivity;
import dev.simon.behold.R;

public class PriestAdapter extends RecyclerView.Adapter<PriestAdapter.PriestViewHolder> {

    Context context;
    List<Channel> chanLi;

    public PriestAdapter(Context context, List<Channel> chanLi){
        this.context = context;
        this.chanLi = chanLi;
    }

    @NonNull
    @Override
    public PriestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.priest_layout, parent, false);
        return new PriestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PriestViewHolder holder, int position) {

        Picasso.get().load(chanLi.get(position).getChan_dp_url()).placeholder(R.drawable.loading).into(holder.priest_dp);
        holder.priest_name.setText(chanLi.get(position).getChan_name());
        holder.pri_church.setText(chanLi.get(position).getChurch_name());
        holder.pri_church_loc.setText(chanLi.get(position).getChurch_loc());

        /*holder.priest_follow.setOnClickListener(View->{
            Toast.makeText(context, "Following!", Toast.LENGTH_SHORT).show();
        });

        holder.pr_rl.setOnClickListener(View->{
            Intent intent = new Intent(context, PriestProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("priest_id", chanLi.get(position).getChan_id());
            intent.putExtras(bundle);
            context.startActivity(intent);
        });*/

    }

    @Override
    public int getItemCount() {
        return chanLi.size();
    }

    public static class PriestViewHolder extends RecyclerView.ViewHolder {

        private final TextView following_num, pri_church, pri_church_loc,priest_name;
        private final CircleImageView priest_dp;
        private final RelativeLayout pr_rl;

        public PriestViewHolder(@NonNull View itemView) {
            super(itemView);

            following_num = itemView.findViewById(R.id.following_num);
            pri_church = itemView.findViewById(R.id.pri_church);
            pri_church_loc = itemView.findViewById(R.id.pri_church_loc);
            priest_name = itemView.findViewById(R.id.priest_name);
            priest_dp = itemView.findViewById(R.id.priest_dp);
            pr_rl = itemView.findViewById(R.id.pr_rl);

        }
    }
}
