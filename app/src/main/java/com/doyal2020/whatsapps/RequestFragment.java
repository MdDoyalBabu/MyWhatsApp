package com.doyal2020.whatsapps;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.doyal2020.whatsapps.Holder.Contacts;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private DatabaseReference ChatRequestRef,userDatabase,contactDatabase;
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

        ChatRequestRef= FirebaseDatabase.getInstance().getReference("WhatsApp").child("ChatRequestID");
        userDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("Users");
        contactDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("Contacts");
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();

        return reqeustView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options=
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatRequestRef.child(currentUserID),Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,ReqeustViewHolder> adapter=
                new FirebaseRecyclerAdapter<Contacts, ReqeustViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ReqeustViewHolder holder, int position, @NonNull Contacts model) {

                        holder.itemView.findViewById(R.id.reqeust_accept_button_Id).setVisibility(View.VISIBLE);
                        holder.itemView.findViewById(R.id.reqeust_cancel_button_Id).setVisibility(View.VISIBLE);

                        final String list_user_id=getRef(position).getKey();

                        DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){

                                    String type=dataSnapshot.getValue().toString();
                                    if (type.equals("received")){

                                        userDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot.hasChild("image")){

                                                        String userImages=dataSnapshot.child("image").getValue().toString();
                                                        Picasso.get().load(userImages).placeholder(R.drawable.profile_icon).into(holder.profileImages);
                                                    }
                                                        final String profileName=dataSnapshot.child("name").getValue().toString();
                                                        final String profileStatus=dataSnapshot.child("status").getValue().toString();


                                                        holder.userName.setText(profileName);
                                                        holder.userStatus.setText("wants to connect with you");


                                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {

                                                            CharSequence options[]=new CharSequence[]
                                                                    {
                                                                            "Accept",
                                                                            "Cancel"
                                                                    };

                                                            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                            builder.setTitle( profileName+  " Chat Request");

                                                            builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {

                                                                    if (which==0){

                                                                        contactDatabase.child(currentUserID).child(list_user_id).child("Contacts")
                                                                                .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()){
                                                                                    contactDatabase.child(list_user_id).child(currentUserID).child("Contacts")
                                                                                            .setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()){


                                                                                                ChatRequestRef.child(currentUserID).child(list_user_id).removeValue()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if (task.isSuccessful()){
                                                                                                                    ChatRequestRef.child(list_user_id).child(currentUserID).removeValue()
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()){
                                                                                                                                        Toast.makeText(getContext(), "New Contact Saved", Toast.LENGTH_SHORT).show();
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });

                                                                    }
                                                                    if (which==1){
                                                                        ChatRequestRef.child(currentUserID).child(list_user_id).removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()){
                                                                                            ChatRequestRef.child(list_user_id).child(currentUserID).removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()){
                                                                                                                Toast.makeText(getContext(), " Contact Deleted", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                });
                                                                    }

                                                                }
                                                            });

                                                            builder.show();

                                                        }
                                                    });


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
                        ReqeustViewHolder holder =new ReqeustViewHolder(view);
                        return holder;
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


