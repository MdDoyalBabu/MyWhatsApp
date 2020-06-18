package com.doyal2020.whatsapps.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.doyal2020.whatsapps.ChatsFragment;
import com.doyal2020.whatsapps.Holder.Messages;
import com.doyal2020.whatsapps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {


    private List<Messages> messagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;

    public MessagesAdapter(List<Messages> messagesList) {
        this.messagesList = messagesList;
    }

    public class MessagesViewHolder extends RecyclerView.ViewHolder {

        TextView senderMessagesText,receiverMessagesText;
        CircleImageView receiverProfileImages;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessagesText=itemView.findViewById(R.id.sender_messages_TextView_Id);
            receiverMessagesText=itemView.findViewById(R.id.receiver_messages_textView_id);
            receiverProfileImages=itemView.findViewById(R.id.messages_profile_image_id);

        }
    }
    @NonNull
    @Override
    public MessagesAdapter.MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layou,parent,false);
      mAuth=FirebaseAuth.getInstance();
       return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesAdapter.MessagesViewHolder holder, int position) {

        String messageSenderId=mAuth.getCurrentUser().getUid();

        Messages messages=messagesList.get(position);

        String fromUserID=messages.getFrom();
        String fromMessageType=messages.getType();


        userDatabase= FirebaseDatabase.getInstance().getReference("WhatsApp").child("Users").child(fromUserID);


        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("image")){

                    String reciverImage=dataSnapshot.child("image").getValue().toString();

                    Picasso.get().load(reciverImage).placeholder(R.drawable.profile_icon).into(holder.receiverProfileImages);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (fromMessageType.equals("text")){

            holder.receiverMessagesText.setVisibility(View.INVISIBLE);
            holder.receiverProfileImages.setVisibility(View.INVISIBLE);

            if (fromUserID.equals(messageSenderId)){

                holder.senderMessagesText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessagesText.setTextColor(Color.BLACK);
                holder.senderMessagesText.setText(messages.getMessage());

            }
            else {

                holder.senderMessagesText.setVisibility(View.INVISIBLE);


                holder.receiverMessagesText.setVisibility(View.VISIBLE);
                holder.receiverProfileImages.setVisibility(View.VISIBLE);

                holder.receiverMessagesText.setBackgroundResource(R.drawable.receiver_messages_layou);
                holder.receiverMessagesText.setTextColor(Color.BLACK);
                holder.receiverMessagesText.setText(messages.getMessage());

            }
        }

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }


}
