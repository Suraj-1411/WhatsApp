package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whatsapp.Adapters.NewGroupAdapter;
import com.example.whatsapp.ModelClasses.ChatModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.DataFormatException;

public class GroupCreate extends AppCompatActivity {
    StorageReference reference;
    Toolbar toolbar;
    String groupIconName = "";
    private static final String TAG = "MyGroupCreate";
    CircularImageView groupIcon, next_btn;
    EditText groupname;
    TextView participents;
    List<String> userIds;
    String participents_number;
    RecyclerView recyclerView;
    List<ChatModel> models;
    DatabaseReference usersRef, GroupRef;
    Uri uri;
    Map<String,String> members;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Groups");
        reference = FirebaseStorage.getInstance().getReference();

        userIds = getIntent().getStringArrayListExtra("users");
        participents_number = getIntent().getExtras().getString("par_no");
        members=new HashMap<>();

        for (String uid:userIds){
            members.put(uid,"");
        }
        FillRecyclerView(userIds);

        toolbar = findViewById(R.id.newGroup_toolBar_id);
        toolbar.setTitle("New group");
        toolbar.setSubtitle("Add subject");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        models = new ArrayList<>();
        groupIcon = findViewById(R.id.newGroup_pic_id);
        groupname = findViewById(R.id.newGroup_name_id);
        next_btn = findViewById(R.id.newgroup_next);
        participents = findViewById(R.id.TV_02);

        participents.setText("Participents:" + participents_number);
        recyclerView = findViewById(R.id.newgroup_recyclerView_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        groupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(GroupCreate.this);

            }
        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(groupname.getText())) {
                    Toast.makeText(GroupCreate.this, "Group Name must be provided", Toast.LENGTH_SHORT).show();
                } else {
                    creategroup();
                }
            }
        });
    }

    private void creategroup() {
        ProgressDialog dialog=new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Creating  "+groupname.getText());
        dialog.setMessage("On progress");
        dialog.show();
        if (uri != null) {
            if (groupIconName != null) {
                reference.child("Groups/" + groupname).child(groupIconName).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            reference.child("Groups/" + groupname).child(groupIconName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    Map<String, String> map = new HashMap<>();
                                    map.put("groupIcon", downloadUrl);
                                    map.put("membersize", String.valueOf(participents_number));
                                    map.put("groupname",groupname.getText().toString());
                                    GroupRef.child(groupname.getText().toString()).child("info").setValue(map);
                                    GroupRef.child(groupname.getText().toString()).child("members").setValue(members);

                                    dialog.cancel();

                                    Intent intent=new Intent(GroupCreate.this,GroupChat.class);
                                    intent.putExtra("GroupName",groupname.getText().toString());
                                    intent.putExtra("Members",participents_number);
                                    intent.putExtra("GroupIcon",downloadUrl);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Log.d(TAG, "onComplete: " + task.getException().getMessage());
                            Toast.makeText(GroupCreate.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
        else{
            Map<String, String> map = new HashMap<>();
            map.put("members", String.valueOf(participents_number));
            map.put("groupname",groupname.getText().toString());
            GroupRef.setValue(map);
        }
    }

    private void FillRecyclerView(List<String> userIds) {

        for (String uid : userIds) {
            usersRef.child(uid).child("Profile").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        ChatModel model = dataSnapshot.getValue(ChatModel.class);
                        models.add(model);
                        NewGroupAdapter adapter = new NewGroupAdapter(models);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                uri = result.getUri();
                groupIcon.setImageURI(uri);
                groupIconName = uri.getLastPathSegment();
            }
        }
    }
}
