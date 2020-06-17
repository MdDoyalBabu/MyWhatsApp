package com.doyal2020.whatsapps;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class RequestFragment extends Fragment {


    private View reqeustView;
    private RecyclerView reqeustRecyclerViewList;
    private DatabaseReference requestDatabase,userDatabase;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        reqeustView= inflater.inflate(R.layout.fragment_request, container, false);
        reqeustRecyclerViewList=reqeustView.findViewById(R.id.request_RecyclerView_List_Id);
        reqeustRecyclerViewList.setLayoutManager(new LinearLayoutManager(getContext()));

        requestDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("ChatRequestID");
        userDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("Users");
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();

        return reqeustView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(requestDatabase.child(currentUserID),Contacts.class)
                .build();


        FirebaseRecyclerAdapter<Contacts,ReqeustViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, ReqeustViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ReqeustViewHolder holder, int position, @NonNull final Contacts model) {

                        holder.itemView.findViewById(R.id.reqeust_accept_button_Id).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.reqeust_cancel_button_Id).setVisibility(View.VISIBLE);

                        final String user_list_id=getRef(position).getKey();

                      DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();

                      getTypeRef.addValueEventListener(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                              if (dataSnapshot.exists()){

                                  String type=dataSnapshot.getValue().toString();

                                  if (type.equals("received")){
                                      userDatabase.child(user_list_id).addValueEventListener(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                              if (dataSnapshot.hasChild("image")){

                                                  String reqeustImage=dataSnapshot.child("image").getValue().toString();
                                                  String reqeustName=dataSnapshot.child("name").getValue().toString();
                                                  String reqeustStates=dataSnapshot.child("status").getValue().toString();


                                                  holder.userName.setText(reqeustName);
                                                  holder.userStatus.setText(reqeustStates);
                                                  Picasso.get().load(reqeustImage).placeholder(R.drawable.profile_icon).into(holder.profileImages);

                                              }
                                              else {
                                                  String reqeustName=dataSnapshot.child("name").getValue().toString();
                                                  String reqeustStates=dataSnapshot.child("status").getValue().toString();


                                                  holder.userName.setText(reqeustName);
                                                  holder.userStatus.setText(reqeustStates);
                                              }

                                          }
                                          @Override
                                          public void onCancelled(@NonNull DatabaseError databaseError) {

                                          }
                                      });
                                  }
                              }

                          }

                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      });

                    }

                    @NonNull
                    @Override
                    public ReqeustViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.username_display_layout,parent,false);

                        ReqeustViewHolder viewHolder=new ReqeustViewHolder(view);
                        return viewHolder;
                    }
                };

        reqeustRecyclerViewList.setAdapter(adapter);
        adapter.startListening();

    }

    public static  class  ReqeustViewHolder extends RecyclerView.ViewHolder {
        TextView userName,userStatus;
        CircleImageView profileImages;
        Button acceptButton,cancelButton;
        public ReqeustViewHolder(@NonNull View itemView) {
            super(itemView);
            userName=itemView.findViewById(R.id.user_profile_nameTextview_id);
            userStatus=itemView.findViewById(R.id.user_status_id);
            profileImages=itemView.findViewById(R.id.user_profile_images_id);
            acceptButton=itemView.findViewById(R.id.reqeust_accept_button_Id);
            cancelButton=itemView.findViewById(R.id.reqeust_cancel_button_Id);
        }
    }

}


