package com.example.whatsapp;

import android.Manifest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Map;
import java.util.Objects;

public class profileActivity extends AppCompatActivity {

    private static final int REQUESTCODE = 100;
    //    long max_bytes=1024*1024*1024;
    public static final int PHOTO_REQUEST_CODE = 123;
    ProgressDialog dialog;
    ProgressBar progressBar;
    String name, about, number, downloadurl, uid, profileName;
    TextInputLayout text;
    CircularImageView profilepic;
    Button cancel, ok;
    TextView txt_name, txt_about, txt_phone;
    StorageReference mreference;
    DatabaseReference mref;
    FirebaseAuth mauth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_name = findViewById(R.id.name_tv_id);
        txt_about = findViewById(R.id.about_tv_id);
        txt_phone = findViewById(R.id.phone_tv_id);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Updating Profile");
        dialog.setMessage("Please wait...");
        dialog.setCanceledOnTouchOutside(false);


        mauth = FirebaseAuth.getInstance();
        uid = mauth.getCurrentUser().getUid();
        progressBar = findViewById(R.id.PGbar_id);
        mref = FirebaseDatabase.getInstance().getReference("Users").child(uid);
        mreference = FirebaseStorage.getInstance().getReference(uid);


        profilepic = findViewById(R.id.profilepic_id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUESTCODE);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUESTCODE);
            }
        }

        setValues();

    }

    private void setValues() {
        mref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String, Object> values = (Map<String, Object>) dataSnapshot.getValue();
                downloadurl = values.get("proImage").toString();
                txt_name.setText((CharSequence) values.get("Name"));
                txt_about.setText((CharSequence) values.get("About"));
                txt_phone.setText((CharSequence) values.get("Phone"));
                Picasso.get().load(downloadurl).placeholder(R.drawable.profile).into(profilepic);
                /*mreference.child("profile/"+profileName).getBytes(max_bytes).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        profilepic.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(MainActivity.TAG, "onFailure: Profile Activity ;called");
                        Log.d(MainActivity.TAG, "onFailure: "+e.getMessage());
                    }
                });*/

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Map<String, Object> values = (Map<String, Object>) dataSnapshot.getValue();
                downloadurl = values.get("proImage").toString();
                txt_name.setText((CharSequence) values.get("Name"));
                txt_about.setText((CharSequence) values.get("About"));
                txt_phone.setText((CharSequence) values.get("Phone"));
                Picasso.get().load(downloadurl).placeholder(R.drawable.profile).into(profilepic);


                /*mreference.child("Profile/"+profileName).getBytes(max_bytes).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        profilepic.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(MainActivity.TAG, "onFailure: Profile Activity ;called");
                        Log.d(MainActivity.TAG, "onFailure: "+e.getMessage());
                    }
                });*/

                Toast.makeText(profileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
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

    public void profile_chooser(View view) {
        Intent pro_chooser = new Intent();
        pro_chooser.setType("image/*");
        pro_chooser.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pro_chooser, "LAUNCH"), PHOTO_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUESTCODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission Access", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PHOTO_REQUEST_CODE && data != null) {

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null) {
                Uri pic = result.getUri();
                profilepic.setImageURI(pic);
                profileName = pic.getLastPathSegment();
                mreference.child("profile/" + profileName).putFile(pic).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        dialog.show();
                        mreference.child("profile/" + profileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                dialog.cancel();
                                downloadurl=uri.toString();
                                mref.child("Profile").child("proImage").setValue(downloadurl);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.cancel();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(), "Profile pic Updated", Toast.LENGTH_SHORT).show();
                            }
                        });
                        if (task.isSuccessful()) {
                            dialog.cancel();
                        } else {
                            Log.d(MainActivity.TAG, "onFailure: profileActivity :called");
                            Log.d(MainActivity.TAG, "onFailure: " + task.getException().getMessage());
                        }
                    }
                });

            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void btn_name_click(View view) {
        View mview = getLayoutInflater().inflate(R.layout.dialogbox, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(profileActivity.this);
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
//                txt_name.setText(name);
                dialog.dismiss();
                mref.child("Profile").child("Name").setValue(name);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(profileActivity.this);
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
//                txt_about.setText(about);
                dialog.dismiss();

                mref.child("Profile").child("About").setValue(about);
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
        final View mview = getLayoutInflater().inflate(R.layout.dialogbox, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(profileActivity.this);
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
//                txt_phone.setText(number);
                dialog.dismiss();
                mref.child("Profile").child("Phone").setValue(number);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }
}
