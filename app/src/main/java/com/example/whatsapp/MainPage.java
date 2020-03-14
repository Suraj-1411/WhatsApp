package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.whatsapp.Adapters.viewpagerAdapter;
import com.example.whatsapp.Fragments.Calls;
import com.example.whatsapp.Fragments.Chats;
import com.example.whatsapp.Fragments.Groups;
import com.example.whatsapp.Fragments.Status;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainPage extends AppCompatActivity {

    Toolbar toolbar;
    AppBarLayout appBarLayout;
    TabLayout tabLayout;
    ViewPager viewPager;
    com.example.whatsapp.Adapters.viewpagerAdapter viewpagerAdapter;
    DatabaseReference reference;
    String currentUser;
//    ActionMode actionMode;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        toolbar = findViewById(R.id.toolbar_id);
        appBarLayout = findViewById(R.id.appbar_id);
        viewPager = findViewById(R.id.viewpager_id);
        tabLayout = findViewById(R.id.tablayout_id);
        viewpagerAdapter = new viewpagerAdapter(getSupportFragmentManager());


        viewpagerAdapter.AddFragments(new Chats(), "CHATS");
        viewpagerAdapter.AddFragments(new Status(), "STATUS");
        viewpagerAdapter.AddFragments(new Calls(), "CALLS");
        viewpagerAdapter.AddFragments(new Groups(),"GROUPS");

        //setting up Tab Layout
        viewPager.setAdapter(viewpagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


        //setting up toolbar
        toolbar.setTitle("Whats App");
        setSupportActionBar(toolbar);

        currentUser= FirebaseAuth.getInstance().getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser).child("Profile");

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm");
        String time = dateFormat.format(calendar.getTime());
        reference.child("lastseen").setValue(time);

        /*Key = getIntent().getExtras().getString(profile_settings.KEY);
        profileName = getIntent().getExtras().getString(profile_settings.PROFILE);*/

    }

    @Override
    protected void onResume() {
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat dateFormat=new SimpleDateFormat("hh:mm");
        String time = dateFormat.format(calendar.getTime());
        reference.child("lastseen").setValue(time);

        boolean networkAvailable = connectivity.isNetworkAvailable(MainPage.this);
        if(!networkAvailable){
            Toast.makeText(this, "Network not Available", Toast.LENGTH_SHORT).show();
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.searchview_id:
                Toast.makeText(MainPage.this, "You clicked Search Button", Toast.LENGTH_SHORT).show();
//                actionMode=startSupportActionMode(mcallback);
                break;
            case R.id.newgroup_id:
                Intent intent=new Intent(MainPage.this,AddpeopleGroup.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(MainPage.this, "Create Your group", Toast.LENGTH_SHORT).show();
                break;
            case R.id.broadcast_id:
                Toast.makeText(MainPage.this, "you clicked New Broadcast option", Toast.LENGTH_SHORT).show();
                break;
            case R.id.whatsappweb_id:
                Toast.makeText(MainPage.this, "you clicked whats App web option", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout_id:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainPage.this, "user loggedout", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings_id:
                Intent settings=new Intent(MainPage.this, settings.class);
                startActivity(settings);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /*ActionMode.Callback mcallback=new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.searchview,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            SearchView searchView= (SearchView) item.getActionView();
            searchView.setQueryHint("Type here");

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    };*/

}
