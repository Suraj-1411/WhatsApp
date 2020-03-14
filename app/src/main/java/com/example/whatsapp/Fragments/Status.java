package com.example.whatsapp.Fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.whatsapp.Adapters.StatusAdapter;
import com.example.whatsapp.ModelClasses.ChatUser;
import com.example.whatsapp.R;
import com.example.whatsapp.ViewStatus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class Status extends Fragment {
    private static final String TAG = "MyStatus";
    CircularImageView status_add_profile, status_profile,add_status_camera;
    RelativeLayout addStatus, status;
    DatabaseReference mystatus, userInfo, setStatus, friendsRef, friendsStatusInfoRef,friendsInforef;
    StorageReference statusStorage;
    String currentUserid = "", downloadUrl, image;
    RecyclerView recyclerView;
    List<ChatUser> users;
    StatusAdapter adapter;

    public Status() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_status, container, false);
        currentUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        status_add_profile = view.findViewById(R.id.status_add_profile);
        status_profile = view.findViewById(R.id.status_profile);
        addStatus = view.findViewById(R.id.RL_01);
        add_status_camera=view.findViewById(R.id.add_status_camera);
        status = view.findViewById(R.id.RL_02);
        mystatus = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserid);
        userInfo = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserid);
        setStatus = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUserid);
        friendsRef=FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid).child("MyFriends");
        friendsStatusInfoRef =FirebaseDatabase.getInstance().getReference().child("Users");
        friendsInforef=FirebaseDatabase.getInstance().getReference().child("Users");
        statusStorage = FirebaseStorage.getInstance().getReference().child(currentUserid);

        recyclerView=view.findViewById(R.id.status_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        users=new ArrayList<>();

        friendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    String uid=ds.getKey();
                    friendsStatusInfoRef.child(uid).child("Status").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if(dataSnapshot.exists()){
                                friendsInforef.child(uid).child("Profile").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        users.clear();
                                        ChatUser user=  dataSnapshot.getValue(ChatUser.class);
                                        users.add(user);
                                        adapter=new StatusAdapter(users);
                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if(dataSnapshot.exists()){
                                friendsInforef.child(uid).child("Profile").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        users.clear();
                                        ChatUser user=  dataSnapshot.getValue(ChatUser.class);
                                        users.add(user);
                                        adapter=new StatusAdapter(users);
                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                friendsInforef.child(uid).child("Profile").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        users.clear();
                                        ChatUser user=  dataSnapshot.getValue(ChatUser.class);
                                        users.add(user);
                                        adapter=new StatusAdapter(users);
                                        recyclerView.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d(TAG, "onCreateView: Called");
        mystatus.child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    addStatus.setVisibility(View.GONE);
                    status.setVisibility(View.VISIBLE);
                    userInfo.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String, String> data = (Map<String, String>) dataSnapshot.getValue();
                            if (data != null) {
                                Picasso.get().load(data.get("proImage")).placeholder(R.drawable.profile).into(status_profile);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    status.setVisibility(View.GONE);
                    addStatus.setVisibility(View.VISIBLE);

                    userInfo.child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Map<String, String> data = (Map<String, String>) dataSnapshot.getValue();
                            if (data != null) {
                                Picasso.get().load(data.get("proImage")).placeholder(R.drawable.profile).into(status_add_profile);
                                addStatus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        CropImage.activity()
                                                .setGuidelines(CropImageView.Guidelines.ON)
                                                .setAspectRatio(1, 1)
                                                .start(Objects.requireNonNull(getContext()),Status.this);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        add_status_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(getContext(),Status.this);
            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), ViewStatus.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("StatusUID",currentUserid);
                startActivity(intent);
            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data!=null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                Uri uri = result.getUri();
                image = uri.getLastPathSegment();
                statusStorage.child("status/" + image).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Status Uploaded", Toast.LENGTH_SHORT).show();
                            statusStorage.child("status/" + image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl = uri.toString();
                                    Log.d(TAG, "onSuccess: "+downloadUrl);
                                    image=image.replaceAll(".jpg","");
                                    setStatus.child("Status").child(String.valueOf(System.currentTimeMillis())).setValue(downloadUrl);
                                }
                            });
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException().getMessage());
                        }
                    }
                });
            }
        }

    }

}


