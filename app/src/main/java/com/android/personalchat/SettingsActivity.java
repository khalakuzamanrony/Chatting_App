package com.android.personalchat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingsActivity extends AppCompatActivity {
    private static final int IMG_REQ = 1;
    StorageTask storageTask;
    private Toolbar toolbar;
    private CircleImageView imageView;
    private TextView name, bio;
    private ImageButton bio_edit;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private byte[] thumb_data, profile_pic_data;
    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        setData();
        bio_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adb = new AlertDialog.Builder(SettingsActivity.this);
                LinearLayout linearLayout = new LinearLayout(SettingsActivity.this);
                final EditText text = new EditText(SettingsActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                adb.setView(text);
                adb.setTitle("Upadte Bio");
                adb.setMessage("Write to Update Bio");
                adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String te = text.getText().toString();
                        String userID2 = firebaseUser.getUid();
                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(userID2);
                        Map map = new HashMap();
                        map.put("status", te);

                        databaseReference2.updateChildren(map);
                    }
                });
                adb.create().show();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getImage = new Intent();
                getImage.setType("image/*");
                getImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(getImage, "Select with.."), IMG_REQ);
                /*CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);*/
            }
        });
    }


    private void init() {
        imageView = findViewById(R.id.profile_image);
        name = findViewById(R.id.profile_name);
        bio = findViewById(R.id.profile_bio);
        bio_edit = findViewById(R.id.bio_edit);
    }

    private void setData() {
        String userID = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String g_name = dataSnapshot.child("name").getValue().toString();
                String g_bio = dataSnapshot.child("status").getValue().toString();
                final String g_profile_pic = dataSnapshot.child("profile_image").getValue().toString();


                name.setText(g_name);
                bio.setText(g_bio);

                if (!g_profile_pic.equals("default")) {
                    Picasso.get().load(g_profile_pic)
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.ic_launcher_background).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(g_profile_pic).placeholder(R.drawable.ic_launcher_background).into(imageView);
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(SettingsActivity.this);
                progressDialog.setTitle("Upload");
                progressDialog.setMessage("Uploading Photo");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                resultUri = result.getUri();
                String imagename = firebaseUser.getUid();
                final File profile_pic_file = new File(resultUri.getPath());
                try {
                    Bitmap profile_pic = new Compressor(SettingsActivity.this)
                            .setMaxHeight(400)
                            .setMaxWidth(400)
                            .setQuality(60)
                            .compressToBitmap(profile_pic_file);

                    ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
                    profile_pic.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayInputStream);
                    profile_pic_data = byteArrayInputStream.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final StorageReference filePath = storageReference.child("Profile_Images").child(imagename + ".jpg");
                final StorageReference thumb_path = storageReference.child("Profile_Images").child("thumb").child(imagename + ".jpg");
                storageTask = filePath.putBytes(profile_pic_data);
                storageTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Uri d = (Uri) task.getResult();
                            final String u = d.toString();
                            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                            Map map = new HashMap();
                            map.put("profile_image", u);
                            databaseReference1.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                        //Thumbnails
                                        final File thumb_file = new File(resultUri.getPath());
                                        try {
                                            Bitmap thumb = new Compressor(SettingsActivity.this)
                                                    .setMaxHeight(50)
                                                    .setMaxWidth(50)
                                                    .setQuality(75)
                                                    .compressToBitmap(thumb_file);

                                            ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
                                            thumb.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayInputStream);
                                            thumb_data = byteArrayInputStream.toByteArray();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        StorageTask storageTask1 = thumb_path.putBytes(thumb_data);
                                        storageTask1.continueWithTask(new Continuation() {
                                            @Override
                                            public Object then(@NonNull Task task) throws Exception {
                                                if (!task.isSuccessful()) {
                                                    throw task.getException();
                                                }
                                                return thumb_path.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    Uri thumb_uri = (Uri) task.getResult();
                                                    final String thumb_url = thumb_uri.toString();
                                                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                                                    Map map2 = new HashMap();
                                                    map2.put("thumb_image", thumb_url);
                                                    databaseReference2.updateChildren(map2);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.hide();
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

}