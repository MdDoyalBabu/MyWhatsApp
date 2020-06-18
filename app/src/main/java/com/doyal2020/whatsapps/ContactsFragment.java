package com.doyal2020.whatsapps;


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
public class ContactsFragment extends Fragment {


    private View contactView;
    private RecyclerView myContactRecyclerViewList;
    private String currentUserID;
    private DatabaseReference contactDatabase,userDatabase;
    private FirebaseAuth mAuth;

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

      contactView= inflater.inflate(R.layout.fragment_contacts, container, false);

      myContactRecyclerViewList=contactView.findViewById(R.id.contact_RecyclerView_List_Id);
      myContactRecyclerViewList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();

        contactDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("Contacts").child(currentUserID);
        userDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("Users");

       return contactView;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactDatabase,Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,ContactViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, ContactViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactViewHolder holder, int position, @NonNull final Contacts model) {


                       String userIDs=getRef(position).getKey();

                       userDatabase.child(userIDs).addValueEventListener(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                               if (dataSnapshot.hasChild("image")){

                                   String userImages=dataSnapshot.child("image").getValue().toString();
                                   String profileName=dataSnapshot.child("name").getValue().toString();
                                   String profileStatus=dataSnapshot.child("status").getValue().toString();

                                   holder.userName.setText(profileName);
                                   holder.userStatus.setText(profileStatus);
                                   Picasso.get().load(userImages).placeholder(R.drawable.profile_icon).into(holder.profileImages);


                               }
                               else {
                                   String profileName=dataSnapshot.child("name").getValue().toString();
                                   String profileStatus=dataSnapshot.child("status").getValue().toString();

                                   holder.userName.setText(profileName);
                                   holder.userStatus.setText(profileStatus);
                               }

                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });


                    }

                    @NonNull
                    @Override
                    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.username_display_layout,parent,false);

                        ContactsFragment.ContactViewHolder viewHolder=new ContactsFragment.ContactViewHolder(view);
                        return viewHolder;
                    }
                };

        myContactRecyclerViewList.setAdapter(adapter);
        adapter.startListening();
    }

    public  static  class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView userName,userStatus;
        CircleImageView profileImages;
        public ContactViewHolder(@NonNull View itemView) {

            super(itemView);
            userName=itemView.findViewById(R.id.user_profile_nameTextview_id);
            userStatus=itemView.findViewById(R.id.user_status_id);
            profileImages=itemView.findViewById(R.id.user_profile_images_id);
        }
    }


}
