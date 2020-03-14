package com.example.whatsapp.Fragments;


import android.content.Intent;
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
import android.widget.Toast;

import com.example.whatsapp.Adapters.ChatsUserAdapter;
import com.example.whatsapp.AddpeopleChat;
import com.example.whatsapp.ModelClasses.ChatUser;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Chats extends Fragment {
    private static final String TAG = "MyChat";
    private static final String TAG1 = "MyChat123";

    RecyclerView recyclerView;
    CircularImageView chats;
    String currentUserID;
    ArrayList<String> ChatUserIds;
    DatabaseReference messagesRef,usersRef;
    List<ChatUser> users;
    ChatsUserAdapter adapter;

    public Chats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_chats, container, false);
        // Inflate the layout for this fragment

        currentUserID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        messagesRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("Messages");
        usersRef=FirebaseDatabase.getInstance().getReference().child("Users");


        chats=view.findViewById(R.id.chats_id);
        users=new ArrayList<>();
        recyclerView=view.findViewById(R.id.recyclerView_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new ChatsUserAdapter(users);
        recyclerView.setAdapter(adapter);
        chats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), AddpeopleChat.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        loadChats();
        return view;
    }

    private void loadChats() {
        Log.d(TAG, "loadChats: Called");

        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ChatUserIds =new ArrayList<>();
                ChatUserIds.add(dataSnapshot.getKey());
                loadUsers(ChatUserIds);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

    private void loadUsers(ArrayList<String> chatUserIds) {

        for(String userId:chatUserIds){
            Log.d(TAG, "loadUsers: UIDS"+userId);
            usersRef.child(userId).child("Profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ChatUser user=dataSnapshot.getValue(ChatUser.class);
                    users.add(user);
                    adapter=new ChatsUserAdapter(users);
                    recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

}
