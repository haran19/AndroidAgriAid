package com.example.haran.agritec;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView myFriendList;
    private DatabaseReference friendsRef,Usersref;
    private FirebaseAuth mAuth;
    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mAuth = FirebaseAuth.getInstance();
        online_user_id=mAuth.getCurrentUser().getUid();
        friendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
       Usersref = FirebaseDatabase.getInstance().getReference().child("Users");



        myFriendList=(RecyclerView) findViewById(R.id.friend_list);
        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this );
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();


    }

    private void DisplayAllFriends()
    {
        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> firebaseRecyclerAdapter
                =new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>
                (
                   Friends.class,
                        R.layout.all_users_display_layout,
                        FriendsViewHolder.class,
                        friendsRef



                 )
        {
            @Override
            protected void populateViewHolder(final FriendsViewHolder viewHolder, Friends model, int position)
            {

                viewHolder.setDate(model.getDate());

                final String userIDs=getRef(position).getKey();

                Usersref.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if(dataSnapshot.exists())
                        {
                            final String userName=dataSnapshot.child("fullname").getValue().toString();
                            final String profileImage=dataSnapshot.child("profileimage").getValue().toString();

                      viewHolder.setFullname(userName);
                      viewHolder.setProfileimage( profileImage);
                      viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                          @Override
                          public void onClick(View v) {
                              CharSequence options[] = new CharSequence[]
                                      {
                                       userName + "'s Profile",
                                              "Send Messge"


                                      };
                              AlertDialog.Builder builder=new AlertDialog.Builder(FriendsActivity.this);
                              builder.setTitle("Select Option");
                              builder.setItems(options, new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {
                                      if (which == 0) {
                                          Intent profileintent = new Intent(FriendsActivity.this, PersonalProfileActivity.class);
                                          profileintent.putExtra("visit_user_id", userIDs);
                                          startActivity(profileintent);
                                      }
                                      if (which == 1) {
                                          Intent chatintent = new Intent(FriendsActivity.this, ChatActivity.class);
                                          chatintent.putExtra("visit_user_id", userIDs);
                                          chatintent.putExtra("userName", userName);
                                          startActivity(chatintent);

                                      }

                                  }

                              });
                              builder.show();

                          }
                      });

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        myFriendList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class FriendsViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView=itemView;
        }
        public void setProfileimage(String profileimage)
        {

            CircleImageView myImage =(CircleImageView)mView.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile).into(myImage);
        }

        public void setFullname(String fullname) {
            TextView myName =(TextView)mView.findViewById(R.id.all_users_profile_name);
            myName.setText(fullname);
        }
        public void setDate(String Date) {
            TextView myDate =(TextView)mView.findViewById(R.id.all_users_status);
            myDate.setText(Date);
        }
    }
}
