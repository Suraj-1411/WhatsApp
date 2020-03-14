package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Adapters.MessageAdapter;
import com.example.whatsapp.ModelClasses.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PrivateChat extends AppCompatActivity {
    private static final String TAG = "MYTAG";
    List<Message> messageList = new ArrayList<>();
    CircularImageView receiversImage;
    RecyclerView recyclerView;
    Toolbar toolbar;
    EditText editText;
    String message = "", img = "";
    String currentuser;
    long senderpushID, receiverpushID;
    String receiverUserID;
    TextView receiverName, receiverlastseen;
    DatabaseReference mesRef, receiveruserRef, messageRef, receiversmesRef, calluser, receiveCall;
    MessageAdapter adapter;
    SinchClient sinchClient;
    Call call;
    AlertDialog alertDialog;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);
        toolbar = findViewById(R.id.chat_toolbar_id);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        receiverUserID = getIntent().getExtras().getString("UID");
        currentuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuser).child("Messages").child(receiverUserID);
        messageRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuser).child("Messages");
        receiveruserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(receiverUserID).child("Profile");
        receiversmesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(receiverUserID).child("Messages");
        calluser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuser).child("Calls");
        receiveCall = FirebaseDatabase.getInstance().getReference().child("Users").child(receiverUserID).child("Calls");


        receiversImage = findViewById(R.id.chat_receiver_image_id);
        receiverName = findViewById(R.id.chat_receiver_name_id);
        receiverlastseen = findViewById(R.id.chat_receiver_lastseen_id);
        editText = findViewById(R.id.message_id);
        recyclerView = findViewById(R.id.chat_recyclerView_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(adapter);
        recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());

        receiveruserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> data = (Map<String, String>) dataSnapshot.getValue();
                if (data != null) {
                    receiverName.setText(data.get("Name"));
                    receiverlastseen.setText(data.get("lastseen"));
                    Picasso.get().load(data.get("proImage")).placeholder(R.drawable.profile).into(receiversImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        loadMessages();

        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(currentuser)
                .applicationKey("483b3536-4ec5-40e2-9dc7-36be9ea5b51c")
                .applicationSecret("Nli1cm+zpEeTWHVekZKIPw==")
                .environmentHost("clientapi.sinch.com")
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());
        sinchClient.start();

    }

    private class SinchCallListener implements CallListener {

        @Override
        public void onCallProgressing(Call call) {
            Toast.makeText(PrivateChat.this, "Ringing", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallEstablished(Call call) {
            Toast.makeText(PrivateChat.this, "", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallEnded(Call endcall) {
            dialog.dismiss();
            Toast.makeText(PrivateChat.this, "Call ended", Toast.LENGTH_SHORT).show();
            call = null;
            endcall.hangup();

        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }

    private class SinchCallClientListener implements CallClientListener {

        @Override
        public void onIncomingCall(CallClient callClient, Call callInfo) {
            CircularImageView imageView, pickup, hang;

            View view = LayoutInflater.from(PrivateChat.this).inflate(R.layout.call_dialog_incoming, null);
            AlertDialog.Builder dialog = new AlertDialog.Builder(PrivateChat.this)
                    .setView(view);

            alertDialog.setCanceledOnTouchOutside(false);

            imageView = view.findViewById(R.id.call_dialog_pic);
            pickup = view.findViewById(R.id.call_pickup);
            hang = view.findViewById(R.id.call_reject);
            pickup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    call = callInfo;
                    call.answer();
                    call.addCallListener(new SinchCallListener());
                }
            });
            hang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                    call = callInfo;
                    call.hangup();
                }
            });
            call.getCallId();
            alertDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.call_chat_toolbar, menu);

        MenuItem menuItem = menu.findItem(R.id.call_user);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                call_user(receiverUserID);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void call_user(String receivercallUserID) {

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat format=new SimpleDateFormat("hh:mm");
        String time=format.format(calendar.getTime());

        Map<String, String> map = new HashMap<>();
        map.put("type", "outgoing");
        map.put("userid", receivercallUserID);
        map.put("time",time);
        calluser.child(String.valueOf(System.currentTimeMillis())).setValue(map);

        Map<String, String> map2 = new HashMap<>();
        map2.put("type", "incoming");
        map2.put("userid", currentuser);
        map2.put("time",time);
        receiveCall.child(String.valueOf(System.currentTimeMillis())).setValue(map2);

        if (call == null) {
            call = sinchClient.getCallClient().callUser(receivercallUserID);
            call.addCallListener(new SinchCallListener());

            OpenDialog(call);
        }
    }

    private void OpenDialog(Call call) {
//        Toast.makeText(this, "Caller ID;"+call.getCallId(), Toast.LENGTH_SHORT).show();
        TextView textView;
        CircularImageView imageView, hang;
        View view = LayoutInflater.from(PrivateChat.this).inflate(R.layout.call_dialog_outgoing, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(view);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);


        imageView = view.findViewById(R.id.call_dialog_pic1);
        hang = view.findViewById(R.id.call_reject1);
        textView = view.findViewById(R.id.call_state_text);

        receiveruserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                if (map != null) {
                    img = map.get("proImage");
                    textView.append("\n" + map.get("Name"));
                    Picasso.get().load(img).placeholder(R.drawable.profile).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        hang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                call.hangup();
                Toast.makeText(PrivateChat.this, "Call ended", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();

    }

    private void loadMessages() {
        mesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Message message =dataSnapshot.getValue(Message.class);
                    messageList.add(message);
                    adapter = new MessageAdapter(messageList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Message message = (Message) dataSnapshot.getValue(Message.class);
                    messageList.add(message);
                    adapter = new MessageAdapter(messageList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    Message message = (Message) dataSnapshot.getValue(Message.class);
                    messageList.add(message);
                    adapter = new MessageAdapter(messageList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
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
        /*long l = System.currentTimeMillis();
        Toast.makeText(this, String.valueOf(l), Toast.LENGTH_SHORT).show();
        Log.d(TAG, "send_message: "+l);*/

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
        String time = dateFormat.format(calendar.getTime());
        message = editText.getText().toString().trim();

        senderpushID = System.currentTimeMillis();
        Map<String, String> Sendermap = new HashMap<>();
        Sendermap.put("message", message);
        Sendermap.put("type", "sent");
        Sendermap.put("time", time);

        messageRef.child(receiverUserID).child(String.valueOf(senderpushID)).setValue(Sendermap);

        receiverpushID = System.currentTimeMillis();
        receiversmesRef.child(currentuser).child(String.valueOf(receiverpushID)).child("message").setValue(message);
        receiversmesRef.child(currentuser).child(String.valueOf(receiverpushID)).child("type").setValue("received");
        receiversmesRef.child(currentuser).child(String.valueOf(receiverpushID)).child("time").setValue(time);

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
        editText.getText().clear();
    }


}

