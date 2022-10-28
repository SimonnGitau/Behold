package dev.simon.UserDashboard.Adapter;

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

import dev.simon.Classes.Event;
import dev.simon.UserDashboard.Activities.EventViewActivity;
import dev.simon.behold.R;

public class EventsFragAdapter extends RecyclerView.Adapter<EventsFragAdapter.EveFragViewHolder> {


    Context context;
    List<Event> events;

    public EventsFragAdapter(Context context, List<Event> events){
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public EventsFragAdapter.EveFragViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.events_frag_layout, parent, false);
        return new EveFragViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsFragAdapter.EveFragViewHolder holder, int position) {

        Picasso.get().load(events.get(position).getEvent_img_uri()).placeholder(R.drawable.loading).into(holder.eve_frag_flyer);
        holder.eve_frag_time.setText(String.format("%s" + " " + "%s", events.get(position).getEvent_time(), events.get(position).getEvent_date()));
        holder.eve_frag_tt.setText(events.get(position).getEvent_title());
        holder.eve_frag_vnu.setText(events.get(position).getEvent_venue());
        holder.eve_frag_int.setText(String.valueOf(events.get(position).getEvent_int()));
        holder.eve_frag_com.setText(String.valueOf(events.get(position).getEvent_com()));

        holder.eve_frag_cv.setOnClickListener(View -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("event_clicked_id", events.get(position).getEvent_id());
            intent.putExtras(bundle);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EveFragViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView eve_frag_cv;
        private final ImageView eve_frag_flyer;
        private final TextView eve_frag_time, eve_frag_tt, eve_frag_vnu, eve_frag_int, eve_frag_com;

        public EveFragViewHolder(@NonNull View itemView) {
            super(itemView);

            eve_frag_cv = itemView.findViewById(R.id.eve_frag_cv);
            eve_frag_flyer = itemView.findViewById(R.id.eve_frag_flyer);
            eve_frag_time = itemView.findViewById(R.id.eve_frag_time);
            eve_frag_tt = itemView.findViewById(R.id.eve_frag_tt);
            eve_frag_vnu = itemView.findViewById(R.id.eve_frag_vnu);
            eve_frag_int = itemView.findViewById(R.id.eve_frag_int);
            eve_frag_com = itemView.findViewById(R.id.eve_frag_com);

        }
    }
}
