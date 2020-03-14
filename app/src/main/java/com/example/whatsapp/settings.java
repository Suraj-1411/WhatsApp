package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class settings extends AppCompatActivity {
//    long max_bytes = 1024 * 1024 * 1024;
    CircularImageView imageView;
    TextView name, about;
    DatabaseReference mref;
    StorageReference mreference;
    FirebaseAuth mauth;
    String uid,downloadurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        imageView = findViewById(R.id.profile_id);
        name = findViewById(R.id.Name_id);
        about = findViewById(R.id.About_id);
        mauth=FirebaseAuth.getInstance();
        uid=mauth.getCurrentUser().getUid();
        Log.d(MainActivity.TAG, "onCreate:settings :Called");
        setvalues();
    }


    private void setvalues() {
        mref = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mreference = FirebaseStorage.getInstance().getReference(uid);

        mref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                if(data!=null) {
                    downloadurl = String.valueOf(data.get("proImage"));
                    name.setText((CharSequence) data.get("Name"));
                    about.setText((CharSequence) data.get("About"));
                    Picasso.get().load(downloadurl).placeholder(R.drawable.profile).into(imageView);
                }

                /*mreference.child("profile/" + profileName).getBytes(max_bytes).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(MainActivity.TAG, "onFailure: settings :called");
                        Log.d(MainActivity.TAG, "onFailure: " + e.getMessage());
                    }
                });*/
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
                downloadurl=data.get("proImage").toString();
                name.setText((CharSequence) data.get("Name"));
                about.setText((CharSequence) data.get("About"));
                Picasso.get().load(downloadurl).into(imageView);

                /*mreference.child("Profile/" + profileName).getBytes(max_bytes).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(MainActivity.TAG, "onFailure: settings :called");
                        Log.d(MainActivity.TAG, "onFailure: " + e.getMessage());
                    }
                });*/

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void profile_click(View view) {

        Intent profileIntent = new Intent(settings.this, profileActivity.class);
        startActivity(profileIntent);
    }

    public void account_click(View view) {
    }

    public void chat_click(View view) {
    }

    public void help_click(View view) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
