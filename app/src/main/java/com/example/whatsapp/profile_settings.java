package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Map;
import java.util.Objects;

import static android.provider.Contacts.SettingsColumns.KEY;
import static com.google.android.gms.common.Scopes.PROFILE;

public class profile_settings extends AppCompatActivity{

    String uid, downloadurl, profile_name;
    ProgressDialog dialog;
    Uri uri,pic;
    TextInputLayout text;
    TextView txt_name, txt_about, txt_number;
    Button cancel, ok, next;
    String name, about, number;
    public static final String KEY = "key";
    public static final String PROFILE = "profile";

    DatabaseReference mref,allusers;
    StorageReference mreference;
    FirebaseAuth mauth;

    private static final int REQUEST_CODE = 111;
    ImageView profilepic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        profilepic = findViewById(R.id.profilepic_id);
        txt_name = findViewById(R.id.name_tv_id);
        txt_about = findViewById(R.id.about_tv_id);
        txt_number = findViewById(R.id.phone_tv_id);
        next = findViewById(R.id.next_id);
        dialog = new ProgressDialog(this);
        mauth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference("Users");
        allusers=FirebaseDatabase.getInstance().getReference("AllUsers");
        uid = mauth.getCurrentUser().getUid();
        mreference = FirebaseStorage.getInstance().getReference(uid);

        /*String phoneNumber = mauth.getCurrentUser().getPhoneNumber();
        Toast.makeText(this, "phoneNumber : " + phoneNumber, Toast.LENGTH_SHORT).show();
        String providerId = mauth.getCurrentUser().getProviderId();
        Toast.makeText(this, "ProviderId : " + providerId, Toast.LENGTH_SHORT).show();*/


        next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                dialog.setTitle("Uploading Profile");
                dialog.setMessage("Please wait...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                if (txt_name.getText().toString().trim().isEmpty()) {
                    dialog.cancel();
                    Toast.makeText(profile_settings.this, "Name Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (txt_about.getText().toString().trim().isEmpty()) {
                    dialog.cancel();
                    Toast.makeText(profile_settings.this, "About Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (txt_number.getText().toString().trim().isEmpty() | txt_number.getText().toString().trim().length() < 10) {
                    dialog.cancel();
                    Toast.makeText(profile_settings.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                insertprofile(name, about, number);
            }
        });

    }


    private void insertprofile(final String name, final String about, final String number) {
        if(profile_name!=null) {
            if (pic != null) {
                mreference.child("profile/" + profile_name).putFile(pic)
                        .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                mreference.child("profile/" + profile_name).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downloadurl=uri.toString();

                                        String phoneNumber = mauth.getCurrentUser().getPhoneNumber();
                                        Toast.makeText(profile_settings.this, phoneNumber, Toast.LENGTH_SHORT).show();

                                        mref.child(uid).child("Profile").child("Name").setValue(name);
                                        mref.child(uid).child("Profile").child("About").setValue(about);
                                        mref.child(uid).child("Profile").child("Phone").setValue(mauth.getCurrentUser().getPhoneNumber());
                                        mref.child(uid).child("Profile").child("UserID").setValue(uid);
                                        mref.child(uid).child("Profile").child("proImage").setValue(downloadurl);

                                        //Inserting Users


                                        allusers.child(uid).setValue("");

                                        Toast.makeText(profile_settings.this, "Got the Url", Toast.LENGTH_SHORT).show();
                                        Log.d(MainActivity.TAG, "onComplete: "+downloadurl);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        dialog.cancel();
                                        Log.d(MainActivity.TAG, "onFailure: Error"+e.getMessage());
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        dialog.cancel();
                                        Intent intent = new Intent(profile_settings.this, MainPage.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                });
                                if (task.isSuccessful()) {
                                    Toast.makeText(profile_settings.this, "Profile picture Uploaded", Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog.cancel();
                                    Toast.makeText(profile_settings.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d(MainActivity.TAG, "onFailure: Profile_settings :called");
                                    Log.d(MainActivity.TAG, "onFailure: " + task.getException().getMessage());
                                }
                            }
                        });
                    }
            }
        }

    public void profile_chooser(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CODE && data != null) {
            uri = data.getData();
            //for cropping Image
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(result!=null) {
                pic = result.getUri();
                profilepic.setImageURI(pic);
                profile_name = pic.getLastPathSegment();
            }
            else {
                Toast.makeText(this, "Image not Selected", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void btn_name_click(View view) {
        View mview = getLayoutInflater().inflate(R.layout.dialogbox, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(profile_settings.this);
        text = mview.findViewById(R.id.txt_id);
        text.setHint("Name");
        cancel = mview.findViewById(R.id.cancel_id);
        ok = mview.findViewById(R.id.Ok_id);

        builder.setView(mview);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = text.getEditText().getText().toString().trim();
                txt_name.setText(name);
                dialog.dismiss();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    public void btn_about_click(View view) {
        View mview = getLayoutInflater().inflate(R.layout.dialogbox, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(profile_settings.this);
        text = mview.findViewById(R.id.txt_id);
        text.setHint("About");
        cancel = mview.findViewById(R.id.cancel_id);
        ok = mview.findViewById(R.id.Ok_id);

        builder.setView(mview);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                about = text.getEditText().getText().toString().trim();
                txt_about.setText(about);
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public void btn_phone_click(View view) {
        View mview = getLayoutInflater().inflate(R.layout.dialogbox, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(profile_settings.this);
        text = mview.findViewById(R.id.txt_id);
        text.setHint("Phone");
        text.getEditText().setInputType(InputType.TYPE_CLASS_PHONE);
        cancel = mview.findViewById(R.id.cancel_id);
        ok = mview.findViewById(R.id.Ok_id);

        builder.setView(mview);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = text.getEditText().getText().toString().trim();
                txt_number.setText(number);
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }


}
