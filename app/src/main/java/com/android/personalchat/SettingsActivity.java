package com.android.personalchat;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.UploadTask;
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
    private static final int IMG_REQ2 = 2;
    private Button pro, cov;
    private String profileLink, thumLink, myid;
    private Toolbar toolbar;
    private ImageView coverImage;
    private CircleImageView imageView, addImage;
    private TextView name, bio;
    private ImageButton bio_edit, add_cover;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    private byte[] thumb_data, profile_pic_data;
    private Uri resultUri, imageUri, coverUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            storageReference = FirebaseStorage.getInstance().getReference();
            myid = firebaseUser.getUid();
        }

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
                        String newBio = text.getText().toString();

                        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Users").child(myid);
                        Map map = new HashMap();
                        map.put("status", newBio);

                        databaseReference2.updateChildren(map);
                    }
                });
                adb.create().show();
            }
        });
        addImage.setOnClickListener(new View.OnClickListener() {
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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(myid);
                databaseReference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String link = dataSnapshot.child("profile_image").getValue().toString();
                        startActivity(new Intent(getApplicationContext(), FullViewImageActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .putExtra("imagelink", link));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        add_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), add_cover, Gravity.END);
                popupMenu.getMenu().add(Menu.NONE, 0, 0, "Add Cover Photo");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == 0) {
                            AddCover();
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
/*        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(myid);
                databaseReference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            String link = dataSnapshot.child("cover_image").getValue().toString();
                            startActivity(new Intent(getApplicationContext(), FullViewImageActivity.class)
                                    .putExtra("coverlink", link));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });*/
        pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        cov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void init() {
        imageView = findViewById(R.id.profile_image);
        addImage = findViewById(R.id.add_profile_image);
        name = findViewById(R.id.profile_name);
        bio = findViewById(R.id.profile_bio);
        bio_edit = findViewById(R.id.bio_edit);
        coverImage = findViewById(R.id.cover_image);
        add_cover = findViewById(R.id.add_cover);
        pro = findViewById(R.id.button);
        cov = findViewById(R.id.button2);
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
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        } else if (requestCode == IMG_REQ2 && resultCode == RESULT_OK) {
            coverUri = data.getData();
            CropImage.activity(coverUri)
                    .setAspectRatio(2, 1)
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

                //Profile
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
                //Putting values to Storage and Obtain Download Url
                final StorageReference filePath = storageReference.child("Profile_Images").child(imagename + ".jpg");
                final StorageReference thumb_path = storageReference.child("Profile_Images").child("thumb").child(imagename + ".jpg");
                thumb_path.putBytes(thumb_data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            thumb_path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    thumLink = task.getResult().toString();
                                }
                            });
                        }
                    }
                });
                filePath.putBytes(profile_pic_data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        profileLink = task.getResult().toString();
                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                                        Map map = new HashMap();
                                        map.put("profile_image", profileLink);
                                        map.put("thumb_image", thumLink);
                                        databaseReference1.updateChildren(map).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "Uploaded!", Toast.LENGTH_LONG).show();
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });

                                    }
                                }
                            });
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

    private void AddCover() {
        Intent getImage = new Intent();
        getImage.setType("image/*");
        getImage.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(getImage, "Select with.."), IMG_REQ2);
    }
}