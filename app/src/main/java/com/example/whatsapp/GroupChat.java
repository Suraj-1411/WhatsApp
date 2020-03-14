package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Adapters.GroupChatAdapter;
import com.example.whatsapp.ModelClasses.GroupChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupChat extends AppCompatActivity {
    private static final String TAG = "MyGroupChat";
    String groupName,Members,icon,currentuserName;
    Toolbar toolbar;
    CircularImageView groupIcon;
    TextView groupname,members;
    RecyclerView recyclerView;
    EditText message;
    DatabaseReference senderRef,groupusers,receiversRef,currentUserRef,getMessagesRef;
    List<GroupChatModel> models;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);


        toolbar=findViewById(R.id.group_toolbar_id);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

         groupName = getIntent().getExtras().getString("GroupName");
         Members = getIntent().getExtras().getString("Members");
         icon = getIntent().getExtras().getString("GroupIcon");

         currentUserRef=FirebaseDatabase.getInstance().getReference().child("Users")
                 .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                 .child("Profile");

        senderRef= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Groups")
                .child(groupName)
                .child("messages");

        getMessagesRef=FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Groups")
                .child(groupName)
                .child("messages");

        groupusers=FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Groups")
                .child(groupName)
                .child("members");

        receiversRef=FirebaseDatabase.getInstance().getReference().child("Users");

        models=new ArrayList<>();
        groupIcon=findViewById(R.id.group_icon_id);
        groupname=findViewById(R.id.group_name_Chat_id);
        members=findViewById(R.id.group_members_id);
        recyclerView=findViewById(R.id.groupChat_recyclerView_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupChat.this));
        message=findViewById(R.id.group_message_id);

        Picasso.get().load(icon).placeholder(R.mipmap.newgroup_users_pic_round).into(groupIcon);
        groupname.setText(groupName);
        members.setText("Members:"+Members);

        currentUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String,String> map= (Map<String, String>) dataSnapshot.getValue();
                currentuserName=map.get("Name");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        getMessages();
    }

    private void getMessages() {
        getMessagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()) {
                    GroupChatModel model = dataSnapshot.getValue(GroupChatModel.class);
                    models.add(model);
                    GroupChatAdapter adapter = new GroupChatAdapter(models);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()) {
                    GroupChatModel model = dataSnapshot.getValue(GroupChatModel.class);
                    models.add(model);
                    GroupChatAdapter adapter = new GroupChatAdapter(models);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    GroupChatModel model = dataSnapshot.getValue(GroupChatModel.class);
                    models.add(model);
                    GroupChatAdapter adapter = new GroupChatAdapter(models);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
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

    public void send_message(View view) {
        if (!(TextUtils.isEmpty(message.getText()))){

            Map<String,String> map=new HashMap<>();
            map.put("message",message.getText().toString());
            map.put("type","sent");
            senderRef.child(String.valueOf(System.currentTimeMillis())).setValue(map);

            groupusers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Map<String,String> map1=new HashMap<>();
                    map1.put("message",message.getText().toString());
                    map1.put("type","received");
                    map1.put("name",currentuserName);

                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                        receiversRef.child(ds.getKey())
                                .child("Groups")
                                .child(groupName)
                                .child("messages").child(String.valueOf(System.currentTimeMillis())).setValue(map1);
                    }
                    message.getText().clear();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    }
}
