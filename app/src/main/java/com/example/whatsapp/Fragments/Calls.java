package com.example.whatsapp.Fragments;


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

import com.example.whatsapp.Adapters.CallsAdapter;
import com.example.whatsapp.ModelClasses.CallsModel;
import com.example.whatsapp.ModelClasses.ChatModel;
import com.example.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Calls extends Fragment {
    private static final String TAG = "MyCalls";
    RecyclerView recyclerView;
    DatabaseReference callsusersRef,calluserInfoRef;
    String currentUsersId;
    List<ChatModel> chatModels=new ArrayList<>();
    List<CallsModel> callsModels=new ArrayList<>();
    public Calls() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        currentUsersId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        callsusersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUsersId).child("Calls");
        calluserInfoRef=FirebaseDatabase.getInstance().getReference().child("Users");
        View view=inflater.inflate(R.layout.fragment_calls, container, false);

        recyclerView=view.findViewById(R.id.calls_recyclerView_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchUsers();
        return view;
    }

    private void fetchUsers() {
        callsModels.clear();
        chatModels.clear();
        callsusersRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()) {
                    CallsModel value = dataSnapshot.getValue(CallsModel.class);
                    callsModels.add(value);
                    if (value != null) {
                        calluserInfoRef.child(value.getUserid()).child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    ChatModel value1 = dataSnapshot.getValue(ChatModel.class);
                                    chatModels.add(value1);
                                    CallsAdapter adapter = new CallsAdapter(chatModels, callsModels);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()) {
                    CallsModel value = dataSnapshot.getValue(CallsModel.class);
                    callsModels.add(value);
                    if (value != null) {
                        calluserInfoRef.child(value.getUserid()).child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    ChatModel value1 = dataSnapshot.getValue(ChatModel.class);
                                    chatModels.add(value1);
                                    CallsAdapter adapter = new CallsAdapter(chatModels, callsModels);
                                    recyclerView.setAdapter(adapter);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
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

}
