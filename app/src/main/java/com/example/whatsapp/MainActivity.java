package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.whatsapp.Adapters.viewpagerAdapter;
import com.example.whatsapp.Fragments.Calls;
import com.example.whatsapp.Fragments.Chats;
import com.example.whatsapp.Fragments.Status;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    public static final String TAG = "MyTag";
    public static final String PHONE_NUMBER = "phone Number";
    private static final int REQUESTCODE = 100;
    private static final int REQUESTCODE_INTERNET = 101;
    private static final int REQUESTCODE_WRITE = 102;
    private static final int REQUESTCODE_RECORD_AUDIO = 103;
    private static final int REQUESTCODE_READ_PHONE_STATE = 104;
    private static final int REQUESTCODE_MODIFY_AUDIO = 105;
    FirebaseAuth mAuth;
    Spinner spinner;
    EditText countrycode;
    Button nextButton;
    TextInputLayout number;
    String country_code;
    String Number;
    String[] permissions={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.INTERNET,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_PHONE_STATE,Manifest.permission.MODIFY_AUDIO_SETTINGS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(EasyPermissions.hasPermissions(this,permissions)){
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }else {
                EasyPermissions.requestPermissions(this,"This Application Needs all of Permissions to work Properly",REQUESTCODE,permissions);
            }
            /*if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUESTCODE);
            }
            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.INTERNET}, REQUESTCODE_INTERNET);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE_WRITE);
            }

            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},REQUESTCODE_RECORD_AUDIO);
            }
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},REQUESTCODE_READ_PHONE_STATE);
            }
            if (checkSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS},REQUESTCODE_MODIFY_AUDIO);
            }*/
        }
        else{
            Toast.makeText(this, "Your Device is not Compatible for this Application", Toast.LENGTH_SHORT).show();
            System.exit(0);
        }

        Log.d(TAG, "Country Names Length :" + Country.COUNTRY_NAMES.length);
        Log.d(TAG, "Country Codes Length :" + Country.COUNTRY_CODES.length);

        spinner = findViewById(R.id.country_names_id);
        countrycode = findViewById(R.id.country_code_id);
        nextButton = findViewById(R.id.next_id);
        number = findViewById(R.id.mobileNumber_id);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(MainActivity.this, MainPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, Country.COUNTRY_NAMES));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                country_code = Country.COUNTRY_CODES[spinner.getSelectedItemPosition()];
                countrycode.setText("+" + country_code);
                Log.d(TAG, "country code" + country_code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean networkAvailable = connectivity.isNetworkAvailable(MainActivity.this);

                if (!networkAvailable) {
                    Toast.makeText(MainActivity.this, "Network Not Available", Toast.LENGTH_SHORT).show();
                    return;
                }

                Number = number.getEditText().getText().toString().trim();
                if (Number.isEmpty() || Number.length() < 10 || Number.length() > 10) {
                    number.setError("Invalid Phone Number");
                    number.requestFocus();
                    return;
                }

                String phoneNumber = "+" + country_code + Number;

                Intent intent = new Intent(MainActivity.this, OTPPage.class);
                intent.putExtra(PHONE_NUMBER, phoneNumber);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

}

class connectivity {

     static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = manager.getActiveNetworkInfo();

        try {
            return info != null && info.isConnectedOrConnecting();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
