package dev.simon.PriestDashboard.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

import dev.simon.Classes.Event;
import dev.simon.Classes.Video;
import dev.simon.PriestDashboard.ViewActivities.ViewMyEvent;
import dev.simon.PriestDashboard.ViewActivities.ViewMyTeaching;
import dev.simon.behold.R;

public class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.MyEventHolder> {


    Context context;
    List<Event> myEventsList;

    public MyEventAdapter(Context context, List<Event> myEventsList) {
        this.context = context;
        this.myEventsList = myEventsList;
    }

    @NonNull
    @Override
    public MyEventAdapter.MyEventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_events_layout, parent, false);
        return new MyEventHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyEventAdapter.MyEventHolder holder, int position) {

        Picasso.get().load(myEventsList.get(position).getEvent_img_uri()).into(holder.p_event_img);
        holder.p_event_time.setText(new StringBuilder().append(myEventsList.get(position).getEvent_time()).append("\t").append(myEventsList.get(position).getEvent_date()).toString());
        holder.p_event_title.setText(myEventsList.get(position).getEvent_title());
        holder.p_event_venue.setText(myEventsList.get(position).getEvent_venue());
        holder.com_count.setText(String.valueOf(myEventsList.get(position).getEvent_com()));
        holder.int_count.setText(String.valueOf(myEventsList.get(position).getEvent_int()));

        holder.eve_cv.setOnClickListener(View -> {
            Intent intent = new Intent(context, ViewMyEvent.class);
            Bundle bundle = new Bundle();
            bundle.putString("eve_dp", myEventsList.get(position).getEvent_img_uri());
            bundle.putString("eve_title", myEventsList.get(position).getEvent_title());
            bundle.putString("eve_des", myEventsList.get(position).getEvent_des());
            bundle.putString("eve_time", myEventsList.get(position).getEvent_time());
            bundle.putString("eve_date", myEventsList.get(position).getEvent_date());
            bundle.putString("eve_venue", myEventsList.get(position).getEvent_venue());
            bundle.putString("eve_id", myEventsList.get(position).getEvent_id());
            intent.putExtras(bundle);
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return myEventsList.size();
    }

    public static class MyEventHolder extends RecyclerView.ViewHolder {

        private final RoundedImageView p_event_img;
        private final TextView p_event_time, p_event_title, p_event_venue, int_count, com_count;
        private final MaterialCardView eve_cv;

        public MyEventHolder(@NonNull View itemView) {
            super(itemView);

            p_event_img = itemView.findViewById(R.id.p_event_img);
            p_event_time = itemView.findViewById(R.id.p_event_time);
            p_event_title = itemView.findViewById(R.id.p_event_title);
            p_event_venue = itemView.findViewById(R.id.p_event_venue);
            int_count = itemView.findViewById(R.id.int_count);
            com_count = itemView.findViewById(R.id.com_count);
            eve_cv = itemView.findViewById(R.id.eve_cv);


        }
    }
}
