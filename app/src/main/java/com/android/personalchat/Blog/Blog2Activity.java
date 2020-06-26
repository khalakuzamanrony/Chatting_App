package com.android.personalchat.Blog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.personalchat.MainActivity;
import com.android.personalchat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import id.zelory.compressor.Compressor;

public class Blog2Activity extends AppCompatActivity {
    private static final int IMG_REQ = 1;
    private ImageView image;
    private EditText caption;
    private Button post;
    private Toolbar toolbar;
    private Uri mainImageUri, imageUri;
    private ProgressDialog progressDialog;
    private byte[] blogImageData;
    private String imagename, myid;
    private StorageReference blogImgRef;
    private DatabaseReference rootRef;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog2);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            blogImgRef = FirebaseStorage.getInstance().getReference();
            rootRef = FirebaseDatabase.getInstance().getReference();
            myid = firebaseUser.getUid();
        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
        //---IMAGE---//
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image...."), IMG_REQ);
            }
        });

        //---Button---//
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String blogText = caption.getText().toString().trim();
                if (!TextUtils.isEmpty(blogText)) {
                    progressDialog = new ProgressDialog(Blog2Activity.this);
                    progressDialog.setTitle("Post");
                    progressDialog.setMessage("Posting....");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    //PrepareImageToUpload
                    if (blogImageData == null) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", myid);
                        hashMap.put("image", "noimage");
                        hashMap.put("text", blogText);
                        hashMap.put("time", String.valueOf(System.currentTimeMillis()));
                        DatabaseReference msgPush = rootRef.child("Blogs").child(myid).push();
                        String pushKey = msgPush.getKey();
                        rootRef.child("Blogs").child(pushKey).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    finish();
                                    progressDialog.dismiss();
                                } else {
                                    Log.d("ERROR", task.getException().getMessage());
                                    progressDialog.hide();
                                }
                            }
                        });

                    } else {
                        imagename = rootRef.push().getKey();
                        final StorageReference path = blogImgRef.child("Blog2_Images").child(myid).child(imagename + ".jpg");
                        path.putBytes(blogImageData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if (task.isSuccessful()) {
                                    path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            final String imgURL = task.getResult().toString();
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("id", myid);
                                            hashMap.put("image", imgURL);
                                            hashMap.put("text", blogText);
                                            hashMap.put("time", String.valueOf(System.currentTimeMillis()));
                                            DatabaseReference msgPush = rootRef.child("Blogs").child(myid).push();
                                            String pushKey = msgPush.getKey();
                                            rootRef.child("Blogs").child(pushKey).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        startActivity(new Intent(getApplicationContext(), MainActivity.class)
                                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                                        finish();
                                                        Log.d("URL", imgURL);
                                                        progressDialog.dismiss();
                                                    } else {
                                                        Log.d("ERROR", task.getException().getMessage());
                                                        progressDialog.hide();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Log.d("ERROR", task.getException().getMessage());
                                }
                            }
                        });
                    }


                }
            }
        });
    }

    private void init() {
        image = findViewById(R.id.new2_image);
        caption = findViewById(R.id.new2_caption);
        post = findViewById(R.id.new_post2_button);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMG_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mainImageUri = data.getData();
            CropImage.activity(mainImageUri)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();

                final File profile_pic_file = new File(imageUri.getPath());
                try {
                    Bitmap blog_pic = new Compressor(Blog2Activity.this)
                            .setMaxHeight(400)//400
                            .setMaxWidth(400)//400
                            .setQuality(75)
                            .compressToBitmap(profile_pic_file);

                    ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
                    blog_pic.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayInputStream);
                    blogImageData = byteArrayInputStream.toByteArray();
                    image.setImageBitmap(blog_pic);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}