package com.example.haran.agritec;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AgroServiceActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private ProgressDialog loadingBar;


    private ImageButton SelectPostImage;
    private Button UpdatePostButton, ViewAgropostButton;
    private EditText PostDescription,Postname,PostPrice,PostOffers, PostLocation;
    private String Description,Item_name,Price,Offers,Location;

    private static final int Gallery_Pick = 1;
    private Uri ImageUri;
    //private String Description;
    private android.widget.Spinner Spinner;
    private String SpinnerSelect;

    private StorageReference PostsimagesReference;
    private DatabaseReference UsersRef, PostsRef;

    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadurl, current_user_id;
    private FirebaseAuth mAuth;
  //  private long countPosts=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agro_service);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();


        PostsimagesReference = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
//        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        PostsRef = FirebaseDatabase.getInstance().getReference().child("AgroShops");

        SelectPostImage = (ImageButton) findViewById(R.id.select_post_image);
        UpdatePostButton = (Button) findViewById(R.id.update_post_button);
        ViewAgropostButton = (Button) findViewById(R.id.view_post_button);
        PostDescription = (EditText) findViewById(R.id.postshop_description);
        Spinner = findViewById(R.id.spinner_agroshops);
        Postname = (EditText) findViewById(R.id.Item_name);
        PostPrice = (EditText) findViewById(R.id.Price);
        PostOffers = (EditText) findViewById(R.id.Offers);
        PostLocation = (EditText) findViewById(R.id.Location);


        loadingBar = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("AgroShop");

        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenGallery();
            }
        });

        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidatePostInfo();
            }
        });

        ViewAgropostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUsertoAgroservicesview();
            }
        });

    }

    private void SendUsertoAgroservicesview() {
        Intent LoginIntent = new Intent(AgroServiceActivity.this, AgroServiceViewActivity.class);
        startActivity(LoginIntent);
    }

    private void ValidatePostInfo() {
       // Description = PostDescription.getText().toString();
        Description=PostDescription.getText().toString();
        SpinnerSelect = Spinner.getSelectedItem().toString();
        Item_name = Postname.getText().toString();
        Price=PostPrice.getText().toString();
        Offers= PostOffers.getText().toString();
        Location=PostLocation.getText().toString();
        if (ImageUri == null) {
            Toast.makeText(this, "Please select post image...", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(Description)) {
           Toast.makeText(this, "Please say something about your image...", Toast.LENGTH_SHORT).show();

       }
        else if (TextUtils.isEmpty(Item_name)) {
            Toast.makeText(this, "Please say something about Item_name...", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Please say something about Price...", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(Offers)) {
            Toast.makeText(this, "Please say something about Offers...", Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(Location)) {
            Toast.makeText(this, "Please say something about Location...", Toast.LENGTH_SHORT).show();

        }
        else {
            loadingBar.setTitle("Add New Post");
            loadingBar.setMessage("Please wait,while we are updating your new Post...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToFirebaseStorage();

        }

    }

    private void StoringImageToFirebaseStorage() {

        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-YYYY");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;


        StorageReference filePath = PostsimagesReference.child("AgroShops").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");
        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                if (task.isSuccessful()) {
                    downloadurl = task.getResult().getDownloadUrl().toString();
                    Toast.makeText(AgroServiceActivity.this, "image Uploaded successfully to storage ...", Toast.LENGTH_SHORT).show();

                    SavingPostInformationToDatabase();

                } else {
                    String message = task.getException().getMessage();
                    Toast.makeText(AgroServiceActivity.this, "Error occured:", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SavingPostInformationToDatabase() {

//
//      PostsRef.addValueEventListener(new ValueEventListener() {
//          @Override
//          public void onDataChange(DataSnapshot dataSnapshot) {
//              if (dataSnapshot.exists())
//              {
//               countPosts =dataSnapshot.getChildrenCount();
//
//              }
//              else
//              {
//               countPosts=0;
//
//              }
//          }
//
//          @Override
//          public void onCancelled(DatabaseError databaseError) {
//
//          }
//      });

        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userfullName = dataSnapshot.child("fullname").getValue().toString();
                    String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_user_id);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("time", saveCurrentTime);
                    postsMap.put("description", Description);
                    postsMap.put("postimage", downloadurl);
                    postsMap.put("profileimage", userProfileImage);
                    postsMap.put("fullname", userfullName);
                   //postsMap.put("Counter", countPosts);
                    postsMap.put("Item_name", Item_name);
                    postsMap.put("Price", Price);
                    postsMap.put("Offers",Offers);
                    postsMap.put("Services", SpinnerSelect);
                    postsMap.put("location",Location);

                    PostsRef.child(current_user_id + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    if (task.isSuccessful()) {
                                        //SendUserToMainActivity();
                                        Toast.makeText(AgroServiceActivity.this, "New Post is updated successfully", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    } else {
                                        Toast.makeText(AgroServiceActivity.this, "Error Occured while updating your post", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }

    }

    //@Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if(id == android.R.id.home)
//        {
//            SendUserToMainActivity();
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    private void SendUserToMainActivity() {
//
//        Intent mainIntent = new Intent(Agroshops.this,Agroshopsview.class);
//        startActivity(mainIntent);
//    }
}
