package dev.simon.UserDashboard.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.simon.Classes.Channel;
import dev.simon.Classes.Event;
import dev.simon.UserDashboard.Activities.EventViewActivity;
import dev.simon.behold.R;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {

    Context context;
    List<Event> events;

    public EventsAdapter(Context context, List<Event> events){
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.events_layout, parent, false);
        return new EventsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {

        Picasso.get().load(events.get(position).getEvent_img_uri()).placeholder(R.drawable.loading).into(holder.event_flyer);
        holder.event_dt_time.setText(String.format("%s" + " " + "%s", events.get(position).getEvent_time(), events.get(position).getEvent_date()));
        holder.event_tt.setText(events.get(position).getEvent_title());
        holder.event_vnu.setText(events.get(position).getEvent_venue());
        holder.event_int.setText(String.valueOf(events.get(position).getEvent_int()));
        holder.event_com.setText(String.valueOf(events.get(position).getEvent_com()));

        holder.event_flyer.setOnClickListener(View -> {
            Intent intent = new Intent(context, EventViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("event_clicked_id", events.get(position).getEvent_id());
            intent.putExtras(bundle);
            context.startActivity(intent);
        });

        holder.btn_eve_int.setOnClickListener(View -> {
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

    public static class EventsViewHolder extends RecyclerView.ViewHolder {

        private final ImageView event_flyer;
        private final TextView event_dt_time;
        private final TextView event_tt;
        private final TextView event_vnu;
        private final TextView event_int;
        private final TextView event_com;
        private final MaterialButton btn_eve_int;

        public EventsViewHolder(@NonNull View itemView) {
            super(itemView);

            event_flyer = itemView.findViewById(R.id.event_flyer);
            event_dt_time = itemView.findViewById(R.id.event_dt_time);
            event_tt = itemView.findViewById(R.id.event_tt);
            event_vnu = itemView.findViewById(R.id.event_vnu);
            event_int = itemView.findViewById(R.id.event_int);
            event_com = itemView.findViewById(R.id.event_com);
            btn_eve_int = itemView.findViewById(R.id.btn_eve_int);

        }
    }
}
