package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import jp.shts.android.storiesprogressview.StoriesProgressView;

import static android.view.MotionEvent.*;

public class ViewStatus extends AppCompatActivity implements StoriesProgressView.StoriesListener {
    int counter=0;

    ImageView imageView,profilePic;
    TextView name;
    StoriesProgressView statusView;
    ArrayList<String> images;
    DatabaseReference statusInfo,userinfo;
    String image = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);


        imageView = findViewById(R.id.image_id);
        profilePic=findViewById(R.id.status_profilePic);
        name=findViewById(R.id.statusview_name);
        statusView = findViewById(R.id.statusView_id);
        images = new ArrayList<>();

        String uid = getIntent().getExtras().getString("StatusUID");
        if (uid == null) {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        statusInfo = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Status");
        userinfo=FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Profile");

        userinfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String,String> map= (Map<String, String>) dataSnapshot.getValue();

                Picasso.get().load(map.get("proImage")).placeholder(R.drawable.profile).into(profilePic);
                name.setText(map.get("Name"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        statusInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    image = (String) ds.getValue();
                    images.add(image);
                }

                statusView.setStoriesCount(images.size());
                statusView.setStoryDuration(5000L);
                statusView.setStoriesListener(ViewStatus.this);
                statusView.startStories(counter);

                Picasso.get().load(images.get(counter)).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statusView.skip();
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                statusView.pause();
                return true;
            }
        });

        imageView.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @Override
            public boolean onGenericMotion(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()== ACTION_DOWN){
                    statusView.reverse();
                }
                return true;
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        statusView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        statusView.destroy();
    }

    @Override
    public void onNext() {
        if(counter<images.size()) {
            counter++;
            Picasso.get().load(images.get(counter)).into(imageView);
        }
    }

    @Override
    public void onPrev() {
        if (counter>0) {
            counter--;
            Picasso.get().load(images.get(counter)).into(imageView);
        }
    }

    @Override
    public void onComplete() {
        finish();
    }

    public void back_btn_click(View view) {
        finish();
    }
}

