package com.doyal2020.whatsapps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doyal2020.whatsapps.Holder.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {


    private RecyclerView friendFriendsrecyclerView_list;
    private Toolbar mToolbar;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mDatabase=FirebaseDatabase.getInstance().getReference("WhatsApp").child("Users");



        titileMethod();

        initializeFileds();

    }

    private void initializeFileds() {

       friendFriendsrecyclerView_list=findViewById(R.id.recyclerView_Friends_list_id);
      friendFriendsrecyclerView_list.setLayoutManager(new LinearLayoutManager(this));


    }

    private void titileMethod() {

        mToolbar=findViewById(R.id.find_friends_toolbar_id);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Find Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Contacts> options=new
                FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(mDatabase,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,FrindFrindsViewHolder>adapter=
                new FirebaseRecyclerAdapter<Contacts, FrindFrindsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FrindFrindsViewHolder holder, final int position, @NonNull Contacts model) {

                        holder.userName.setText(model.getName());
                        holder.userStatus.setText(model.getStatus());

                        Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_icon).into(holder.profileImages);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String visit_user_id=getRef(position).getKey();


                                Intent intent=new Intent(FindFriendsActivity.this,UserProfileActivity.class);
                                intent.putExtra("visit_user_id",visit_user_id);
                                startActivity(intent);

                            }
                        });


                    }

                    @NonNull
                    @Override
                    public FrindFrindsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.username_display_layout,parent,false);

                         FrindFrindsViewHolder viewHolder=new FrindFrindsViewHolder(view);

                        return viewHolder;
                    }
                };

        friendFriendsrecyclerView_list.setAdapter(adapter);
        adapter.startListening();

      }

      public  static  class FrindFrindsViewHolder extends RecyclerView.ViewHolder {


        TextView userName,userStatus;
        CircleImageView profileImages;

          public FrindFrindsViewHolder(@NonNull View itemView) {
              super(itemView);

              userName=itemView.findViewById(R.id.user_profile_nameTextview_id);
              userStatus=itemView.findViewById(R.id.user_status_id);
              profileImages=itemView.findViewById(R.id.user_profile_images_id);

          }
      }


}
