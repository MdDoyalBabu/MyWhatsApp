package com.doyal2020.whatsapps;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.doyal2020.whatsapps.Holder.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {



    private View privateChatView;
    private DatabaseReference chatDatabase,userDatabase;
    private FirebaseAuth mAuth;
    private  String currentUserID;
    private RecyclerView chatRecyclerViewListl;


    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        privateChatView= inflater.inflate(R.layout.fragment_chats, container, false);


        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();

        chatRecyclerViewListl=privateChatView.findViewById(R.id.chat_RecyclerView_list_id);
        chatRecyclerViewListl.setLayoutManager(new LinearLayoutManager(getContext()));

        chatDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("Contacts").child(currentUserID);
        userDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("Users");


        return privateChatView;


    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatDatabase,Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts,ChatViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, ChatViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatViewHolder holder, int position, @NonNull Contacts model) {

                        final String userIDs=getRef(position).getKey();
                        final String[] chatImages = {"default_image"};

                        userDatabase.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){
                                    if (dataSnapshot.hasChild("image")){

                                        chatImages[0] =dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(chatImages[0]).placeholder(R.drawable.profile_icon).into(holder.profileImages);

                                    }
                                    final   String chatName=dataSnapshot.child("name").getValue().toString();
                                    final String chatStatus=dataSnapshot.child("status").getValue().toString();


                                    holder.userName.setText(chatName);
                                    holder.userStatus.setText("Last seen: "+" \n"+"Date "+"Time");


                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Intent intent=new Intent(getContext(),ChatActivity.class);

                                            intent.putExtra("visit_user_id",userIDs);
                                            intent.putExtra("visit_user_name",chatName);
                                            intent.putExtra("visit_user_image", chatImages[0]);
                                            startActivity(intent);

                                        }
                                    });

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.username_display_layout,parent,false);
                        return new ChatViewHolder(view);
                    }

                };
            chatRecyclerViewListl.setAdapter(adapter);
            adapter.startListening();
    }

    public static  class  ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userName,userStatus;
        CircleImageView profileImages;
        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            userName=itemView.findViewById(R.id.user_profile_nameTextview_id);
            userStatus=itemView.findViewById(R.id.user_status_id);
            profileImages=itemView.findViewById(R.id.user_profile_images_id);

        }
    }

}
