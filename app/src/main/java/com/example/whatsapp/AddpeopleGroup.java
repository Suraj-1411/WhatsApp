package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.whatsapp.Adapters.GroupAdapter;
import com.example.whatsapp.ModelClasses.Model;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddpeopleGroup extends AppCompatActivity {
    FloatingActionButton button;
    public static final ArrayList<String> numbersList = new ArrayList<>();
    public static final ArrayList<String> Phonenumbers = new ArrayList<>();
    public static final ArrayList<String> uids = new ArrayList<>();
    public static final String TAG = "Hello";
    private static final String TAG1 = "hii";
    Toolbar toolbar;
    RecyclerView recyclerView;
    DatabaseReference alluseresRef, usersPhnRef,usersInfoRef,searchUsersInfo;
    FirebaseAuth mauth;
    String phone, uid, phoneNumber;
    List<Model> modelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpeople_group);

        button=findViewById(R.id.group_next_id);
        modelList =new ArrayList<>();
        toolbar = findViewById(R.id.Toolbar_id);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        alluseresRef = FirebaseDatabase.getInstance().getReference().child("AllUsers");
        usersPhnRef = FirebaseDatabase.getInstance().getReference().child("Users");
        usersInfoRef=FirebaseDatabase.getInstance().getReference().child("Users");
        searchUsersInfo=FirebaseDatabase.getInstance().getReference().child("Users");
        mauth = FirebaseAuth.getInstance();
        phoneNumber = mauth.getCurrentUser().getPhoneNumber();
        toolbar.setTitle("New group");
        toolbar.setSubtitle("Add Participents");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 0);
            }
        }
        AddContacts();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                method(GroupAdapter.selectedUserIds);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void AddtoRecyclerView(String uid) {

        Log.d(TAG, "AddtoRecyclerView: " + uid);

        usersInfoRef.child(uid).child("Profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Model model=dataSnapshot.getValue(Model.class);
                modelList.add(model);
                GroupAdapter adapter=new GroupAdapter(modelList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
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
        alluseresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    uid = ds.getKey();
                    storeUserData(uid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void storeUserData(final String uid) {
        usersPhnRef.child(uid).child("Profile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Log.d(MainActivity.TAG, "onDataChange: "+UID);
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

        getMenuInflater().inflate(R.menu.searchview, menu);
        MenuItem menuItem=menu.findItem(R.id.search_View);
        SearchView searchView= (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAgain(query);
                //Log.d(TAG, "onQueryTextSubmit: "+query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAgain(newText);
                Log.d(TAG1, "onQueryTextChange: "+newText);
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

        GroupAdapter adapter=new GroupAdapter(model);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void method(List<String> data){
        if(data!=null) {
            Intent intent=new Intent(AddpeopleGroup.this,GroupCreate.class);
            intent.putStringArrayListExtra("users", (ArrayList<String>) data);
            intent.putExtra("par_no",String.valueOf(data.size()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

    }

}
