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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetingsActivity extends AppCompatActivity {

    private Toolbar mToolabr;

    private EditText userName,userProfName,userAbout,userAddress,userEmail,userPhoneNo,userDOB;
    private Button UpdateAccountsettingbutton,locationsettingsbutton;

    private CircleImageView userProfImage;

    private DatabaseReference SettingsuserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;
    final static int Gallery_Pick =1;


    private ProgressDialog loadingBar;
    private StorageReference UserProfileImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setings);


        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        SettingsuserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);



        mToolabr=(Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolabr);
        getSupportActionBar().setTitle("Account Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        userName = (EditText)findViewById(R.id.settings_username);
        userProfName = (EditText)findViewById(R.id.settings_profile_full_name);
        userAbout= (EditText)findViewById(R.id.settings_status);
        userAddress = (EditText)findViewById(R.id.settings_country);
        userEmail = (EditText)findViewById(R.id.settings_gender);
        userPhoneNo= (EditText)findViewById(R.id.settings_relationship_status);
        userDOB = (EditText)findViewById(R.id.settings_dob);
        userProfImage =(CircleImageView) findViewById(R.id.settings_profile_image);

        loadingBar = new ProgressDialog(this);



        UpdateAccountsettingbutton=(Button) findViewById(R.id.update_account_settings_buttons);
       locationsettingsbutton=(Button) findViewById(R.id.my_profile_view_location);

        locationsettingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SetingsActivity.this,LocationSet.class);
                startActivity(i);
            }
        });

       SettingsuserRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot)
           {
               if(dataSnapshot.exists())
               {
                   String myProfileImage =dataSnapshot.child("profileimage").getValue().toString();
                   String myUserName =dataSnapshot.child("username").getValue().toString();
                   String myProfileName =dataSnapshot.child("fullname").getValue().toString();
                   String myProfileAbout =dataSnapshot.child("about").getValue().toString();
                   String myDOB =dataSnapshot.child("dob").getValue().toString();
                   String myaddress =dataSnapshot.child("address").getValue().toString();
                   String myemail =dataSnapshot.child("email").getValue().toString();
                   String myphoneno =dataSnapshot.child("phoneno").getValue().toString();

                   Picasso.get().load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                   userName.setText(myUserName);
                   userProfName .setText(myProfileName);
                   userAbout.setText(myProfileAbout);
                   userDOB.setText(myDOB);
                   userAddress.setText( myaddress);
                   userEmail.setText(myemail);
                   userPhoneNo.setText( myphoneno );

               }

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

        UpdateAccountsettingbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidateAccountInfo();

            }
        });


userProfImage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_Pick);
    }
});

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Gallery_Pick && resultCode == RESULT_OK &&  data !=null)
        {
            Uri ImageUri =data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio( 1,1)
                    .start(this);

        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result =CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait,while we are updating your profile Image...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SetingsActivity.this,"Profile Image stored successfully to Firebase storage...",Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            SettingsuserRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if(task.isSuccessful())
                                            {
                                                Intent selfIntent = new Intent(SetingsActivity.this,SetingsActivity.class);
                                                startActivity(selfIntent);


                                                Toast.makeText(SetingsActivity.this,"Profile Image stored to Firebase Database Successfully",Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                            else
                                            {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SetingsActivity.this,"Error" +message,Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }


                                        }
                                    });
                        }
                    }
                });

            }
            else
            {
                Toast.makeText(SetingsActivity.this,"error Occured:Image can't be cropped tryagain",Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }

        }
    }






    private void ValidateAccountInfo()
    {
       String username = userName.getText().toString();
        String Profilename = userProfName.getText().toString();
        String About = userAbout.getText().toString();
        String dob = userDOB.getText().toString();
        String address = userAddress.getText().toString();
        String email = userEmail.getText().toString();
        String phoneno = userPhoneNo.getText().toString();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this,"Please write your username...",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Profilename))
        {
            Toast.makeText(this,"Please write your Profilename...",Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(About))
        {
            Toast.makeText(this,"Please write your about...",Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(dob))
        {
            Toast.makeText(this,"Please write your Date of birth ...",Toast.LENGTH_SHORT).show();

        }
        else if (TextUtils.isEmpty(address))
        {
            Toast.makeText(this,"Please write your address...",Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"Please write your Email...",Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(phoneno))
        {
            Toast.makeText(this,"Please write your Phone No...",Toast.LENGTH_SHORT).show();

        }
        else
        {
            loadingBar.setTitle("Profile Image");
            loadingBar.setMessage("Please wait,while we are updating your profile Image...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            UpdateAccountInformation(username,Profilename,About,dob,address,email,phoneno);
        }
    }

    private void UpdateAccountInformation(String username, String profilename, String About, String dob, String address, String email, String phoneno) {
        HashMap userMap = new HashMap();
        userMap.put("username", username);
        userMap.put("fullname", profilename);
        userMap.put("about", About);
        userMap.put("dob", dob);
        userMap.put("address", address);
        userMap.put("email", email);
        userMap.put("phoneno", phoneno);

        SettingsuserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    SendUserToMainActivity();
                    Toast.makeText(SetingsActivity.this, "Account Settings Updated Successfully...", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
                else
                {
                    Toast.makeText(SetingsActivity.this, "Error Occured while updating account information...", Toast.LENGTH_SHORT).show();
                     loadingBar.dismiss();
                }

            }
        });

    }
        private void SendUserToMainActivity()
        {
            Intent mainIntent =new Intent(SetingsActivity.this,MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();
        }


}
