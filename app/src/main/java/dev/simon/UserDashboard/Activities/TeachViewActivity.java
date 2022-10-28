package dev.simon.UserDashboard.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.simon.Classes.Event;
import dev.simon.Classes.Teach;
import dev.simon.behold.R;

public class TeachViewActivity extends AppCompatActivity {

    private ImageView teach_dp, teach_love;
    private TextView teach_likes, teach_tt, teach_sub, teach_verse, teach_des;

    private SweetAlertDialog lDialog;
    String teach_clicked_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teach_view);

        teach_dp = findViewById(R.id.teach_dp);
        teach_love = findViewById(R.id.teach_love);
        teach_likes = findViewById(R.id.teach_likes);
        teach_tt = findViewById(R.id.teach_tt);
        teach_sub = findViewById(R.id.teach_sub);
        teach_verse = findViewById(R.id.teach_verse);
        teach_des = findViewById(R.id.teach_des);


        lDialog = new SweetAlertDialog(TeachViewActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
        lDialog.setTitleText("Loading...");
        lDialog.setCancelable(true);
        lDialog.show();

        Bundle bundle = getIntent().getExtras();
        teach_clicked_id = bundle.getString("teach_clicked_id");

        updateView(teach_clicked_id);

    }

    private void updateView(String teach_clicked_id) {
        lDialog.show();

        FirebaseFirestore.getInstance().collection("Teachings")
                .document(teach_clicked_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        lDialog.dismiss();
                        if (documentSnapshot != null){
                            Teach current_teach = documentSnapshot.toObject(Teach.class);
                            assert current_teach != null;

                            Picasso.get().load(current_teach.getTeach_img()).placeholder(R.drawable.loading).into(teach_dp);

                            teach_likes.setText(String.valueOf(current_teach.getTeach_likes()));
                            teach_tt.setText(current_teach.getTeach_topic());
                            teach_sub.setText(current_teach.getTeach_sub());
                            teach_verse.setText(current_teach.getTeach_verses());
                            teach_des.setText(current_teach.getTeach_des());

                            lDialog.dismiss();

                            teach_love.setOnClickListener(View->{
                                updateLikes(current_teach);
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        lDialog.dismiss();
                        lDialog = new SweetAlertDialog(TeachViewActivity.this, SweetAlertDialog.ERROR_TYPE);
                        lDialog.getProgressHelper().setBarColor(Color.parseColor("#004EA1"));
                        lDialog.setTitleText(e.getMessage());
                        lDialog.setCancelable(true);
                        lDialog.show();
                    }
                });

    }

    private void updateLikes(Teach current_teach) {
        if (!current_teach.isTeach_liked()) {
            int fin_int = current_teach.getTeach_likes() + 1;
            FirebaseFirestore.getInstance().collection("Teachings")
                    .document(current_teach.getTeach_id())
                    .update("teach_likes", fin_int,
                            "teach_liked", true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            updateView(teach_clicked_id);
                            teach_love.setImageResource(R.drawable.love);
                            teach_love.setColorFilter(R.color.red);
                            teach_likes.setText(String.valueOf(current_teach.getTeach_likes()));
                            Toast.makeText(TeachViewActivity.this, "Thank you!", Toast.LENGTH_LONG).show();
                        }
                    });

        }else {
            int fin_int1 = current_teach.getTeach_likes() - 1;
            FirebaseFirestore.getInstance().collection("Teachings")
                    .document(current_teach.getTeach_id())
                    .update("teach_likes", fin_int1,
                            "teach_liked", false)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            updateView(teach_clicked_id);
                            teach_love.setImageResource(R.drawable.love_outline);
                            teach_love.setColorFilter(R.color.red);
                            teach_likes.setText(String.valueOf(current_teach.getTeach_likes()));
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateView(teach_clicked_id);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateView(teach_clicked_id);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateView(teach_clicked_id);
    }

}