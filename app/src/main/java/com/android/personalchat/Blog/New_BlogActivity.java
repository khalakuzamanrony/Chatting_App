package com.android.personalchat.Blog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.personalchat.R;
import com.android.personalchat.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class New_BlogActivity extends AppCompatActivity {
    private static final int IMG_REQ = 1;
    private ImageView image;
    private EditText caption;
    private Button post;
    private Toolbar toolbar;
    private Uri mainImageUri, imageUri;
    private ProgressDialog progressDialog;
    private byte[] blogImageData;
    private String imagename,myid;
    private StorageReference blogImgRef;
    private DatabaseReference rootRef;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__blog);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser!=null)
        {
            blogImgRef= FirebaseStorage.getInstance().getReference();
            rootRef= FirebaseDatabase.getInstance().getReference();
            myid=firebaseUser.getUid();
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
                progressDialog = new ProgressDialog(New_BlogActivity.this);
                progressDialog.setTitle("Upload");
                progressDialog.setMessage("Uploading Photo");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                imagename= rootRef.push().getKey();
                final StorageReference path=blogImgRef.child("Blog_Images").child(myid).child(imagename+".jpg");
                path.putBytes(blogImageData).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful())
                        {
                           path.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    String imgURL=task.getResult().toString();
                                    Log.d("URL",imgURL);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                        else {
                            Log.d("ERROR",task.getException().getMessage());
                            progressDialog.hide();
                        }
                    }
                });
            }
        });
    }

    private void init() {
        image = findViewById(R.id.new_image);
        caption = findViewById(R.id.new_caption);
        post = findViewById(R.id.new_post_button);
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
                    Bitmap blog_pic = new Compressor(New_BlogActivity.this)
                            .setMaxHeight(400)
                            .setMaxWidth(400)
                            .setQuality(10)
                            .compressToBitmap(profile_pic_file);

                    ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
                    blog_pic.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayInputStream);
                    blogImageData = byteArrayInputStream.toByteArray();
                    image.setImageBitmap(blog_pic);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}