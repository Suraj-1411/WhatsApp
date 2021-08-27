package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.whatsapp.Adapters.ChatAdapter;
import com.example.whatsapp.ModelClasses.Model;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddpeopleChat extends AppCompatActivity {
    private static final String TAG = "ThisTag";
    Toolbar toolbar;
    public static final ArrayList<String> numbersList = new ArrayList<>();
    public static final ArrayList<String> Phonenumbers = new ArrayList<>();
    public static final ArrayList<String> uids = new ArrayList<>();
    RecyclerView recyclerView;
    DatabaseReference alluseresRef, usersPhnRef,usersInfoRef,searchUsersInfo,addFriendsRef;
    FirebaseAuth mauth;
    String phone, uid, phoneNumber;
    List<Model> modelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpeople_chat);
        toolbar=findViewById(R.id.chat_addPeople_toolbar_id);

        toolbar.setTitle("Select contact");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        modelList =new ArrayList<>();
        modelList.add(new Model("https://firebasestorage.googleapis.com/v0/b/whats-app-31bb4.appspot.com/o/group.png?alt=media&token=6b9dc2ad-c24d-4e25-b8ef-86906ddb0695","New group","create a new group",""));
        recyclerView = findViewById(R.id.chatsRV_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        alluseresRef = FirebaseDatabase.getInstance().getReference().child("AllUsers");
        usersPhnRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersInfoRef=FirebaseDatabase.getInstance().getReference().child("Users");
        searchUsersInfo=FirebaseDatabase.getInstance().getReference().child("Users");
        addFriendsRef=FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mauth = FirebaseAuth.getInstance();
        phoneNumber = mauth.getCurrentUser().getPhoneNumber();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 0);
            }
        }
        AddContacts();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void AddtoRecyclerView(String uid) {

        usersInfoRef.child(uid).child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Model model=dataSnapshot.getValue(Model.class);
                modelList.add(model);
                ChatAdapter adapter=new ChatAdapter(modelList,getApplicationContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                addFriendsRef.child("MyFriends").child(uid).setValue("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void AddContacts() {

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        phone = "";
        if (cursor != null) {
            while (cursor.moveToNext()) {
                phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                numbersList.add(phone);
                         /*Intent intent=new Intent(ContactsContract.Intents.Insert.ACTION);
                           intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);*/
            }
        }
        for(String number:numbersList){
            if(!(Phonenumbers.contains(number))){
                Phonenumbers.add(number);
            }
        }
        alluseresRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                uid = dataSnapshot.getKey();
                storeUserData(uid);
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


    private void storeUserData(final String uid) {

        usersPhnRef.child(uid).child("Profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, String> data = (Map<String, String>) dataSnapshot.getValue();
                if (data != null) {
                    String number = data.get("Phone");
                    if (!(number.equals(phoneNumber))) {
                        for (String phone : Phonenumbers) {
                            if (phone.equals(number)) {
                                uids.add(uid);
                                AddtoRecyclerView(uid);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchview,menu);

        MenuItem menuItem=menu.findItem(R.id.search_View);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAgain(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAgain(newText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    private void filterAgain(final String text) {

        List<Model> model=new ArrayList<>();
        for(Model groupModel: modelList){
            if(groupModel.getName().toLowerCase().contains(text.toLowerCase())){
                model.add(groupModel);
            }
        }

        ChatAdapter adapter=new ChatAdapter(model,getApplicationContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
//publish your app on playstore with modification
//change the name of the app and color
