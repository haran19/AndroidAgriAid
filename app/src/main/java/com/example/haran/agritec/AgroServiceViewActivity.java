package com.example.haran.agritec;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AgroServiceViewActivity extends AppCompatActivity {

    private RecyclerView postList;
    private Toolbar mToolbar;//home tool bar

//    private CircleImageView NavProfileImage;
//    private TextView NavProfileUserName;

    private android.widget.ImageButton  AddNewPostButton;

    private FirebaseAuth mAuth;//firebase 1st
    private DatabaseReference UsersRef ,PostsRef,LikesRef,Agroshopsref;

    private android.widget.Spinner Spinner;
    private String SpinnerSelect;
    private ImageButton SearchButton;


    String currentUserID;
   // Boolean Likechecker=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agro_service_view);

        mAuth =FirebaseAuth.getInstance();//firebase 1st
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef=FirebaseDatabase.getInstance().getReference().child("AgroService");
        LikesRef=FirebaseDatabase.getInstance().getReference().child("AgroServiceLikes");
        Agroshopsref = FirebaseDatabase.getInstance().getReference().child("AgroShops");



        mToolbar =(Toolbar) findViewById(R.id.main_page_toolbar);
        SearchButton =(ImageButton) findViewById(R.id.search_services);
        setSupportActionBar(mToolbar);//setting up home tool bar
        getSupportActionBar().setTitle("AgroShopfeed");//set title for action bar



      //  AddNewPostButton =(android.widget.ImageButton) findViewById(R.id.add_new_post_button);
        Spinner   = findViewById(R.id.shop_view_spinner);

        postList =(RecyclerView) findViewById(R.id.all_users_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this );
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);



        Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                SpinnerSelect = Spinner.getSelectedItem().toString();
                DisplayAllUserPosts(  SpinnerSelect);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                SpinnerSelect = Spinner.getSelectedItem().toString();
                DisplayAllUserPosts(  SpinnerSelect);
            }
        });






//        AddNewPostButton.setOnClickListener(new View.OnClickListener (){
//            @Override
//            public void onClick(View v)
//            {
//                SendUserToAgroshops();
//            }
//        });
//
//
//        DisplayAllUserPosts();

    }

    private void DisplayAllUserPosts(String SpinnerSelect )
    {

      //  Query sortPostsinDescendingOrder = PostsRef.orderByChild("Counter");

        Query ServicesQuery  =Agroshopsref.orderByChild("Services")
                .startAt(SpinnerSelect).endAt(SpinnerSelect+ "\uf8ff");

        FirebaseRecyclerAdapter<agservice,PostViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<agservice, PostViewHolder>
                        (
                                agservice.class,
                                R.layout.agro_service_layout,
                                PostViewHolder.class,
                                ServicesQuery
                        )
                {
                    @Override
                    protected void populateViewHolder(PostViewHolder viewHolder, agservice model, int position)
                    {
                        final  String  PostKey= getRef(position).getKey();

                        viewHolder.setFullname(model.getFullname());
                        viewHolder.setTime(model.getTime());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setProfileimage(model.getProfileimage());
                        viewHolder.setPostimage(model.getPostimage());
                        viewHolder.setItem_name(model.getItem_name());
                        viewHolder.setPrice(model.getPrice());
                        viewHolder.setOffers(model.getOffers());
                        viewHolder.setLocation(model.getLocation());


                       // viewHolder.setLikeButtonStatus(PostKey);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                Intent clickPostIntent = new Intent (AgroServiceViewActivity.this,AgroServiceClickPostActivity.class);
                                clickPostIntent.putExtra( "PostKey",PostKey);
                                startActivity(clickPostIntent);


                            }
                        });


//                        viewHolder.CommentPostButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v)
//                            {
//                                Intent commentsIntent = new Intent (Agroshopsview.this,FarmerCommentsActivity.class);
//                                commentsIntent.putExtra( "PostKey",PostKey);
//                                startActivity(commentsIntent);
//
//
//                            }
//                        });


//                        viewHolder.LikepostButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v)
//                            {
//                                Likechecker=true;
//
//                                LikesRef.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot)
//                                    {
//                                        if(Likechecker.equals(true))
//                                        {
//                                            if (dataSnapshot.child(PostKey).hasChild(currentUserID)) {
//                                                LikesRef.child(PostKey).child(currentUserID).removeValue();
//                                                Likechecker = false;
//                                            }
//                                            else
//                                            {
//                                                LikesRef.child(PostKey).child(currentUserID).setValue(true);
//                                                Likechecker = false;
//                                            }
//                                        }
//
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }
//                        });


                    }
                };
        postList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        ImageButton LikepostButton,CommentPostButton;
        TextView DisplayNoOfLikes;
        int  countLikes;
        String currentUserId;
        DatabaseReference LikesRef;


       public PostViewHolder(View itemView) {
            super(itemView);
           mView= itemView;
//
//            LikepostButton = (ImageButton)mView.findViewById(R.id.like_button);
//            CommentPostButton = (ImageButton)mView.findViewById(R.id.comment_button);
//            DisplayNoOfLikes =(TextView)mView.findViewById(R.id.display_no_of_likes);
//
////            LikesRef =FirebaseDatabase.getInstance().getReference().child("FarmerLikes");
//            LikesRef =FirebaseDatabase.getInstance().getReference().child("AgroServiceLikes");
//            currentUserId =FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

//        public void setLikeButtonStatus(final String PostKey)
//        {
//            LikesRef.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot)
//                {
//                    if(dataSnapshot.child(PostKey).hasChild(currentUserId))
//                    {
//                        countLikes =(int )dataSnapshot.child(PostKey).getChildrenCount();
//                        LikepostButton.setImageResource(R.drawable.like);
//                        DisplayNoOfLikes.setText(Integer.toString(countLikes)+(" Likes"));
//                    }
//                    else
//                    {
//                        countLikes =(int )dataSnapshot.child(PostKey).getChildrenCount();
//                        LikepostButton.setImageResource(R.drawable.dislike);
//                        DisplayNoOfLikes.setText(Integer.toString(countLikes)+(" Likes"));
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//
//        }

        public void setFullname(String fullname)
        {
            TextView username =(TextView) mView.findViewById(R.id.post_user_name);
            username.setText(fullname);
        }
        public void setProfileimage(String profileimage)
        {
            CircleImageView image =(CircleImageView) mView.findViewById(R.id.post_profile_image);
            Picasso.get().load(profileimage).into(image);
        }

        public void setTime(String time)
        {
            TextView PostTime =(TextView) mView.findViewById(R.id.post_time);
            PostTime.setText("  "+time);
        }

        public void setDate(String date) {
            TextView PostDate = (TextView) mView.findViewById(R.id.post_date);
            PostDate.setText("  "+date);
        }
        public void setDescription(String description)
        {
            TextView PostDescription = (TextView) mView.findViewById(R.id.post_description);
            PostDescription.setText(description);
        }

        public void setPostimage(String postimage)
        {
            ImageView Postimage =(ImageView) mView.findViewById(R.id.post_image);
            Picasso.get().load(postimage).into(Postimage);
        }

        public void setItem_name(String Item_name)
        {
            TextView Displayname=(TextView)mView.findViewById(R.id.post_Item_name);
            Displayname.setText(Item_name);
        }
        public void  setPrice(String Price)
        {

            TextView DisplayPrice=(TextView)mView.findViewById(R.id.Price);
            DisplayPrice.setText("Price "+Price);
        }
        public void setOffers(String Offers)
        {
            TextView DisplayOffers=(TextView)mView.findViewById(R.id.Offers);
            DisplayOffers.setText("Offers "+Offers);
        }
        public void setLocation(String Location)
        {
            TextView DisplayOffers=(TextView)mView.findViewById(R.id.post_Location);
            DisplayOffers.setText("Location"+Location);
        }

    }




    private void SendUserToAgroshops(){

        Intent addNewPosstIntent = new Intent(AgroServiceViewActivity.this,AgroServiceActivity.class);
        startActivity(addNewPosstIntent);

    }



}
