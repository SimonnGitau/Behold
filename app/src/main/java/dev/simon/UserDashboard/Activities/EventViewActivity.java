package dev.simon.UserDashboard.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import dev.simon.Classes.Event;
import dev.simon.PriestDashboard.ViewActivities.ViewMyEvent;
import dev.simon.UserDashboard.Adapter.EventsAdapter;
import dev.simon.behold.R;

public class EventViewActivity extends AppCompatActivity {

    private TextView eView_time, eView_vnu, eView_tt, eView_int, eView_com, context_eView;
    private MaterialButton btn_eView;
    private ImageView event_flyer;

    private SweetAlertDialog lDialog;

    String event_clicked_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_view);

        eView_time = findViewById(R.id.eView_time);
        eView_vnu = findViewById(R.id.eView_vnu);
        eView_tt = findViewById(R.id.eView_tt);
        eView_int = findViewById(R.id.eView_int);
        eView_com = findViewById(R.id.eView_com);
        context_eView = findViewById(R.id.context_eView);
        btn_eView = findViewById(R.id.btn_eView);
        event_flyer = findViewById(R.id.event_flyer);

        lDialog = new SweetAlertDialog(EventViewActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(true);
        lDialog.show();

        Bundle bundle = getIntent().getExtras();
        event_clicked_id = bundle.getString("event_clicked_id");

        updateView(event_clicked_id);

    }

    private void updateView(String event_clicked_id) {

        lDialog.show();

        FirebaseFirestore.getInstance().collection("Events")
                .document(event_clicked_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        lDialog.dismiss();
                        if (documentSnapshot != null){
                            Event current_event = documentSnapshot.toObject(Event.class);
                            assert current_event != null;

                            lDialog.dismiss();
                            Picasso.get().load(current_event.getEvent_img_uri()).placeholder(R.drawable.loading).into(event_flyer);
                            eView_time.setText(String.format("%s\t%s", current_event.getEvent_time(), current_event.getEvent_date()));
                            eView_vnu.setText(current_event.getEvent_venue());
                            eView_tt.setText(current_event.getEvent_title());
                            eView_int.setText(String.valueOf(current_event.getEvent_int()));
                            context_eView.setText(current_event.getEvent_des());
                            eView_com.setText(String.valueOf(current_event.getEvent_com()));

                            btn_eView.setOnClickListener(View->{
                                        updateLikes(current_event);
                                    });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        lDialog.dismiss();
                        lDialog = new SweetAlertDialog(EventViewActivity.this, SweetAlertDialog.ERROR_TYPE);
                        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                        lDialog.setTitleText(e.getMessage());
                        lDialog.setCancelable(true);
                        lDialog.show();
                    }
                });
    }

    private void updateLikes(Event current_event) {
        if (!current_event.isEvent_interested()) {
            int fin_int = current_event.getEvent_int() + 1;
            FirebaseFirestore.getInstance().collection("Events")
                    .document(current_event.getEvent_id())
                    .update("event_int", fin_int,
                            "event_interested", true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            updateView(event_clicked_id);
                            btn_eView.setBackgroundColor(ContextCompat.getColor(EventViewActivity.this, R.color.blue));
                            btn_eView.setTextColor(ContextCompat.getColor(EventViewActivity.this, R.color.white));
                            btn_eView.setText("Come with your friends too!");
                            eView_int.setText(String.valueOf(current_event.getEvent_int()));
                            Toast.makeText(EventViewActivity.this, "Thank you!", Toast.LENGTH_LONG).show();
                        }
                    });

        }else {
            int fin_int1 = current_event.getEvent_int() - 1;
            FirebaseFirestore.getInstance().collection("Events")
                    .document(current_event.getEvent_id())
                    .update("event_int", fin_int1,
                            "event_interested", false)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            updateView(event_clicked_id);
                            btn_eView.setBackgroundColor(ContextCompat.getColor(EventViewActivity.this, R.color.white));
                            btn_eView.setTextColor(ContextCompat.getColor(EventViewActivity.this, R.color.blue));
                            btn_eView.setText("Interested");
                            eView_int.setText(String.valueOf(current_event.getEvent_int()));
                            Toast.makeText(EventViewActivity.this, "Not interested!", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView(event_clicked_id);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateView(event_clicked_id);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateView(event_clicked_id);
    }
}